package org.example.model;

public record ShiftChoice(
        int id,
        String name
)
{
    @Override
    public String toString()
    {
        return name;
    }
}