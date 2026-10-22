DROP TYPE title cascade;
DROP TABLE shift_type CASCADE;
DROP TABLE employee CASCADE;
DROP TABLE assigned_shift CASCADE;
DROP TABLE login_data CASCADE;




CREATE TYPE title AS ENUM ('doctor', 'technician', 'admin');


CREATE TABLE shift_type (
                            shift_type_id INTEGER PRIMARY KEY,
                            shift_name TEXT UNIQUE,
                            shift_start_time TIME,
                            shift_end_time TIME
);


INSERT INTO shift_type (shift_name, shift_start_time, shift_end_time) VALUES ('Early Morning', '7:30:0','18:30:0');
INSERT INTO shift_type (shift_name, shift_start_time, shift_end_time) VALUES ('8:30 Morning', '8:30:0','19:30:0');
INSERT INTO shift_type (shift_name, shift_start_time, shift_end_time) VALUES ('Late Doctor', '9:30:0','20:30:0');
INSERT INTO shift_type (shift_name, shift_start_time, shift_end_time) VALUES ('Surgery', '7:30:0','18:30:0');
INSERT INTO shift_type (shift_name, shift_start_time, shift_end_time) VALUES ('Night Shift', '20:00:00', '8:00:00');
SELECT * FROM shift_type;


CREATE TABLE employee (
                          employee_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          employee_name TEXT NOT NULL,
                          employee_title title NOT NULL
);




INSERT INTO employee (employee_name, employee_title) VALUES ('Bob','doctor');
INSERT INTO employee (employee_name, employee_title) VALUES ('Janice','doctor');
INSERT INTO employee (employee_name, employee_title) VALUES ('Jerry','technician');
INSERT INTO employee (employee_name, employee_title) VALUES ('Sally','technician');
INSERT INTO employee (employee_name, employee_title) VALUES ('Lucas','admin');


CREATE TYPE time_off AS ENUM ('vacation', 'sick');


DROP TABLE employee_time_off;
CREATE TABLE employee_time_off (
                                   employee_id INTEGER REFERENCES employee(employee_id),
                                   date DATE,
                                   type time_off
);


INSERT INTO employee_time_off(employee_id, date, type) VALUES (1,'9-29-2026','sick');


DROP TABLE assigned_shift;
CREATE TABLE assigned_shift (
                                shift_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                employee_id INTEGER REFERENCES employee(employee_id),
                                shift_date DATE,
                                shift_type_id INTEGER REFERENCES shift_type(shift_type_id),
                                shift_range TSRANGE,
                                shift_requested BOOLEAN DEFAULT false
);


DELETE FROM assigned_shift WHERE true;
INSERT INTO assigned_shift (employee_id, shift_date, shift_type_id) VALUES (2, '2026-9-22', 4);
INSERT INTO assigned_shift (employee_id, shift_date, shift_type_id) VALUES (2, '2026-9-24', 4);
INSERT INTO assigned_shift (employee_id, shift_date, shift_type_id) VALUES (2, '2026-9-26', 4);


SELECT * FROM assigned_shift;


SELECT * FROM shift_type;
CREATE TABLE login_data (
                            eid INTEGER REFERENCES employee(employee_id) PRIMARY KEY,
                            username TEXT NOT NULL UNIQUE,
                            password TEXT NOT NULL
);

-- Seed the one system administrator with the required first-login credentials.
INSERT INTO login_data (eid, username, password)
SELECT employee_id, 'admin', 'admin'
FROM employee
WHERE employee_title = 'admin'
ORDER BY employee_id
LIMIT 1;


CREATE OR REPLACE FUNCTION check_time_off()
    RETURNS TRIGGER AS $$
DECLARE


BEGIN
    -- Need to check if employee has already used to many vacation days/sick days


    if NEW.type = 'vacation' AND NEW.date - CURRENT_DATE <= 7 THEN
        RAISE EXCEPTION
            'Must ask for vacation days more then a week in advance';
end if;


    if NEW.type = 'sick' AND NEW.date - CURRENT_DATE <= 0  THEN
        RAISE EXCEPTION
            'Cannot ask for sick days day of';
end if;


RETURN NEW;
end;
$$ LANGUAGE plpgsql;




CREATE OR REPLACE TRIGGER time_off_trigger
    BEFORE INSERT OR UPDATE
                                   ON employee_time_off
                                   FOR EACH ROW
                                   EXECUTE FUNCTION check_time_off();






-- NEW SHIFT REQUIREMENTS
CREATE OR REPLACE FUNCTION set_and_check_shift()
    RETURNS TRIGGER AS $$
DECLARE
start_t TIME;
    end_t TIME;
    week_start DATE;
BEGIN


    -- SET THE DATE RANGE
SELECT st.shift_start_time, st.shift_end_time INTO start_t, end_t
FROM shift_type AS st WHERE st.shift_type_id = NEW.shift_type_id;

    -- Shift type 5 is used throughout the week, but on Sundays it runs
    -- from its normal start time until 20:00.
    IF EXTRACT(ISODOW FROM NEW.shift_date) = 7
        AND NEW.shift_type_id = 5 THEN
        end_t := TIME '20:00:00';
    END IF;


NEW.shift_range := tsrange(
            NEW.shift_date + start_t,
            NEW.shift_date + end_t +
            CASE WHEN end_t <= start_t THEN INTERVAL '1 day' ELSE INTERVAL '0 days' END,
            '[)'
                       );


    -- NO MORE THAN 2 SURGERY SHIFTS
    if NEW.shift_type_id = 4 THEN
        week_start := date_trunc('week', NEW.shift_date)::date;


        IF (SELECT COUNT(*)
            FROM assigned_shift AS a JOIN shift_type AS st ON st.shift_type_id = a.shift_type_id


            WHERE a.employee_id = NEW.employee_id
              AND a.shift_id IS DISTINCT FROM NEW.shift_id
                                       AND st.shift_type_id = 4
                                       AND a.shift_date >= week_start
                                       AND a.shift_date <  week_start + 7
                                       ) >= 2
                                       THEN
                                       RAISE EXCEPTION
                                       'Employee can not add a third surgery shift in one week';
end if;
end if;


    -- NO MORE THAN 2 NIGHT SHIFTS
    if NEW.shift_type_id = 5 THEN
        week_start := date_trunc('week', NEW.shift_date)::date;


        IF (SELECT COUNT(*)
            FROM assigned_shift AS a JOIN shift_type AS st ON st.shift_type_id = a.shift_type_id


            WHERE a.employee_id = NEW.employee_id
              AND a.shift_id IS DISTINCT FROM NEW.shift_id
                       AND st.shift_type_id = 5
                       AND a.shift_date >= week_start
                       AND a.shift_date <  week_start + 7
                       ) >= 2
                       THEN
                       RAISE EXCEPTION
                       'Employee can not add a third night shift in one week';
end if;
end if;


    -- CHECK IF SHIFT WITHIN 10 HOURS ALREADY EXISTS
    IF EXISTS (SELECT 1 FROM assigned_shift AS a
               WHERE NEW.employee_id = a.employee_id
                 AND a.shift_id IS DISTINCT FROM NEW.shift_id
                 AND (NEW.shift_range &&
                      tsrange(
                              lower(a.shift_range) - INTERVAL '10 hours',
                              upper(a.shift_range) + INTERVAL '10 hours',
                              '[)'
                      )
                   )
    ) THEN
        RAISE EXCEPTION
            'Employee has shift within a 10 hour range';
END IF;


RETURN NEW;
END;
$$ LANGUAGE plpgsql;




CREATE OR REPLACE TRIGGER assigned_shift_trigger
    BEFORE INSERT OR UPDATE OF employee_id, shift_date, shift_type_id
                     ON assigned_shift
                         FOR EACH ROW
                         EXECUTE FUNCTION set_and_check_shift();





SELECT * FROM employee_time_off;
DROP TABLE employee_time_off;
TRUNCATE TABLE employee_time_off;

CREATE TABLE employee_time_off (
                                   eto_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                   employee_id INTEGER REFERENCES employee(employee_id),
                                   date DATE,
                                   type time_off
);

INSERT INTO employee_time_off(employee_id, date, type) VALUES (1,'11-5-2026','sick');

-- NEW TIME OFF REQUIREMENTS
CREATE OR REPLACE FUNCTION check_time_off()
RETURNS TRIGGER AS $$
DECLARE

BEGIN
    -- ALREADY HAVE TIME OFF
    IF EXISTS (SELECT *
               FROM employee_time_off as eto
               WHERE eto.employee_id = NEW.employee_id
               AND eto.eto_id is DISTINCT FROM NEW.eto_id
               AND date_part('day', eto.date) = date_part('day', NEW.date)
               ) THEN
        RAISE EXCEPTION
            'Employee already has day off';
end if;
    -- USED ALL VACATION DAYS
    IF NEW.type = 'vacation' AND  (SELECT COUNT(*)
               FROM employee_time_off AS eto
               WHERE NEW.eto_id IS DISTINCT FROM eto.eto_id
                 AND NEW.employee_id = eto.employee_id
                 AND eto.type = 'vacation'
                 AND  date_part('year', eto.date) = date_part('year', NEW.date)) >= 8
    THEN
        RAISE EXCEPTION
            'Employee has already used 4 sick days';
end if;

    -- USED ALL SICK DAYS
    IF NEW.type = 'sick' AND (SELECT COUNT(*)
               FROM employee_time_off AS eto
               WHERE NEW.eto_id IS DISTINCT FROM eto.eto_id
                 AND NEW.employee_id = eto.employee_id
                 AND eto.type = 'sick'
                 AND  date_part('year', eto.date) = date_part('year', NEW.date)) >= 4
        THEN
        RAISE EXCEPTION
            'Employee has already used 4 sick days';
end if;

    -- VACATION REQUEST TOO EARLY
    if NEW.type = 'vacation' AND NEW.date - CURRENT_DATE <= 21 THEN
        RAISE EXCEPTION
        'Must ask for vacation days three weeks in advance';
end if;

    -- SICK REQUEST TOO EARLY
    if NEW.type = 'sick' AND NEW.date - CURRENT_DATE <= 0  THEN
        RAISE EXCEPTION
            'Cannot ask for sick days day of';
end if;

RETURN NEW;
end;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER time_off_trigger
    BEFORE INSERT OR UPDATE
                                   ON employee_time_off
                                   FOR EACH ROW
                                   EXECUTE FUNCTION check_time_off();


