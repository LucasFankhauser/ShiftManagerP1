package org.example;

import java.time.LocalDate;
import java.time.LocalTime;

public class Shift {

    private LocalDate date;
    private String name;
    private LocalTime start;
    private LocalTime end;

    public Shift(LocalDate date, String name,
                 LocalTime start, LocalTime end) {
        this.date = date;
        this.name = name;
        this.start = start;
        this.end = end;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }
}