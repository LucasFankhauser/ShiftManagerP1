package org.example.admin.schedule;

import org.example.Main.Database;
import org.example.model.Employee;
import org.example.model.EmployeeArray;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class ScheduleGenerator {
    public static void generate_schedule(LocalDate start, LocalDate end) {
        try ( Connection connection = Database.getConnection();) {
            EmployeeArray employees = new EmployeeArray(
                    ScheduleDao.get_all_employees("doctor"),
                    ScheduleDao.get_all_employees("technician")
            );
            employees.connection = connection;

            boolean filled_schedule = true;

            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                if (!fill_day(date, employees)) {
                    filled_schedule = false;
                }
            }

            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                fill_day_priority(date, employees);
            }

            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                fill_day_extra(date, employees);
            }

            if (!filled_schedule) {
                System.out.println("Some shifts cannot be filled");
            } else {
                System.out.println("Schedule filled successfully");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void drop_all_shifts() {
        ScheduleDao.drop_all_shifts();
    }

    public static boolean fill_day(LocalDate date, EmployeeArray employees) {

        boolean filled_day = true;

        if (date.getDayOfWeek() != DayOfWeek.SUNDAY) {

            for (int shift_type = 1; shift_type <= 5; shift_type++) {
                if (!fill_shift(date, employees, shift_type, 1, false)) {
                    filled_day = false;
                }
            }

        } else {

            // Sunday shift: 1 doctor, 2 technicians
            if (!fill_shift(date, employees, 4, 2, false)) {
                filled_day = false;
            }

            // Overnight shift: 1 doctor, 1 technician
            if (!fill_shift(date, employees, 6, 1, false)) {
                filled_day = false;
            }
        }

        return filled_day;
    }

    public static void fill_day_priority(LocalDate date, EmployeeArray employees) {
        if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
            fill_shift(date, employees, 3, 1, true);
        } else if (date.getDayOfWeek() == DayOfWeek.SATURDAY){
            fill_shift(date, employees, 1, 1, true);
        }
    }

    public static void fill_day_extra(LocalDate date, EmployeeArray employees) {
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
            fill_shift(date, employees, 2, 1, true);
            fill_shift(date, employees, 3, 1, true);
            fill_shift(date, employees, 4, 1, true);
        } else if (date.getDayOfWeek() == DayOfWeek.FRIDAY){
            fill_shift(date, employees, 1, 1, true);
            fill_shift(date, employees, 2, 1, true);
            fill_shift(date, employees, 4, 1, true);
        } else if (date.getDayOfWeek() != DayOfWeek.SUNDAY){
            fill_shift(date, employees, 1, 1, true);
            fill_shift(date, employees, 2, 1, true);
            fill_shift(date, employees, 3, 1, true);
            fill_shift(date, employees, 4, 1, true);
        }
    }


    public static boolean fill_shift( LocalDate date, EmployeeArray employees, int shift_type, int tech_count, boolean check_max_shifts) {

        boolean filled_shift = true;

        // Assign one doctor
        Employee first_doctor = employees.get_doctor();
        boolean doctor_assigned = false;

        do {
            Employee doctor = employees.get_doctor();
            if (!check_max_shifts || employees.can_work_more(doctor)) {
                try {
                    ScheduleDao.insertShift(employees.connection, doctor.getId(), date, shift_type, false);
                    doctor_assigned = true;
                    employees.increment_shift_count(doctor);
                } catch (SQLException e) {
                }
            }

            employees.next_doctor();

        } while (!doctor_assigned &&
                employees.get_doctor() != first_doctor);

        if (!doctor_assigned) {
            System.out.println(
                    "Failed to assign doctor to shift "
                            + shift_type + " on " + date
            );
            filled_shift = false;
        }

        // Assign technicians
        for (int i = 0; i < tech_count; i++) {

            Employee first_tech = employees.get_tech();
            boolean tech_assigned = false;

            do {
                Employee tech = employees.get_tech();

                try {
                    ScheduleDao.insertShift(employees.connection, tech.getId(), date, shift_type, false
                    );

                    tech_assigned = true;
                    employees.increment_shift_count(tech);

                } catch (SQLException e) {
                    // Insert failed, try the next technician
                }

                employees.next_tech();

            } while (!tech_assigned &&
                    employees.get_tech() != first_tech);

            if (!tech_assigned) {
                System.out.println(
                        "Failed to assign technician to shift "
                                + shift_type + " on " + date
                );
                filled_shift = false;
            }
        }

        return filled_shift;
    }
}
