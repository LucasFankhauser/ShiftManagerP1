package org.example.model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

public class EmployeeArray {
    public Connection connection;
    ArrayList<Employee> doctors;
    Iterator<Employee> doctor_iterator;
    Employee current_doctor;

    ArrayList<Employee> techs;
    Iterator<Employee> tech_iterator;
    Employee current_tech;
    private final HashMap<Integer, Integer> shiftCounts = new HashMap<>();

    public int get_shift_count(Employee employee) {
        return shiftCounts.getOrDefault(employee.getId(), 0);
    }

    public void increment_shift_count(Employee employee) {
        shiftCounts.merge(employee.getId(), 1, Integer::sum);
    }

    public void reset_shift_counts() {
        shiftCounts.clear();
    }

    public boolean can_work_more(Employee employee) {
        return get_shift_count(employee) < 4;
    }

    public EmployeeArray(ArrayList<Employee> doctors, ArrayList<Employee> techs) {

        if (doctors.isEmpty() || techs.isEmpty()) {
            throw new IllegalArgumentException(
                    "Doctor and technician lists cannot be empty"
            );
        }

        this.doctors = doctors;
        this.techs = techs;

        doctor_iterator = doctors.iterator();
        tech_iterator = techs.iterator();

        next_doctor();
        next_tech();

        for (Employee doctor : doctors) {
            shiftCounts.put(doctor.getId(), 0);
        }

        for (Employee tech : techs) {
            shiftCounts.put(tech.getId(), 0);
        }
    }

    public Employee get_doctor() {
        return current_doctor;
    }

    public void next_doctor() {
        if (!doctor_iterator.hasNext()) {
            doctor_iterator = doctors.iterator();
        }

        current_doctor = doctor_iterator.next();
    }

    public Employee get_tech() {
        return current_tech;
    }

    public void next_tech() {
        if (!tech_iterator.hasNext()) {
            tech_iterator = techs.iterator();
        }

        current_tech = tech_iterator.next();
    }

    public void sort_employees_by_shifts() {
        doctors.sort(Comparator.comparingInt(
                doctor -> shiftCounts.get(doctor.getId())
        ));

        techs.sort(Comparator.comparingInt(
                tech -> shiftCounts.get(tech.getId())
        ));

        doctor_iterator = doctors.iterator();
        tech_iterator = techs.iterator();
    }

    public double get_average_shift_count() {
        return shiftCounts.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }
}