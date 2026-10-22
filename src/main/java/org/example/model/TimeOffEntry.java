package org.example.model;

import java.time.LocalDate;

public record TimeOffEntry(
        int employeeId,
        LocalDate date,
        TimeOffType type
) {}