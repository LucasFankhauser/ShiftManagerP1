package org.example.model;

public enum TimeOffType
{
    VACATION("vacation"),
    SICK("sick");

    private final String databaseValue;

    TimeOffType(String databaseValue)
    {
        this.databaseValue = databaseValue;
    }

    public String getDatabaseValue()
    {
        return databaseValue;
    }

    public static TimeOffType fromDatabase(String value)
    {
        for (TimeOffType type : values())
        {
            if (type.databaseValue.equalsIgnoreCase(value))
            {
                return type;
            }
        }

        throw new IllegalArgumentException(
                "Unknown time-off type: " + value
        );
    }
}