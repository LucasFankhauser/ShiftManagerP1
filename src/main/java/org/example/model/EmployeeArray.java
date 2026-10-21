package org.example.model;

import java.util.ArrayList;
import java.util.Iterator;

public class EmployeeArray {
    ArrayList<Employee> doctors;
    Iterator<Employee> doctor_iterator;
    Employee current_doctor;

    ArrayList<Employee> techs;
    Iterator<Employee> tech_iterator;
    Employee current_tech;

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
}