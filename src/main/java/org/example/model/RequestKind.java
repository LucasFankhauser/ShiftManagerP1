package org.example.model;

public enum RequestKind
{
    WORK("Request to work"),
    TIME_OFF("Request time off");

    private final String displayName;

    RequestKind(String displayName)
    {
        this.displayName = displayName;
    }

    @Override
    public String toString()
    {
        return displayName;
    }
}