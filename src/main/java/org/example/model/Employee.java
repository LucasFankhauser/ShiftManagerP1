package org.example.model;

public class Employee
{
    private final int id;
    private final String name;
    private final String title;

    public Employee(int id, String name, String title)
    {
        this.id = id;
        this.name = name;
        this.title = title;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDisplayName()
    {
        if ("doctor".equals(title))
        {
            return "Dr. " + name;
        }

        if ("technician".equals(title))
        {
            return "Technician " + name;
        }

        return name;
    }
}