package org.example.model;

public record RequestDecision(
        boolean approved,
        String message
)
{
    public static RequestDecision approved(String message)
    {
        return new RequestDecision(true, message);
    }

    public static RequestDecision denied(String message)
    {
        return new RequestDecision(false, message);
    }
}