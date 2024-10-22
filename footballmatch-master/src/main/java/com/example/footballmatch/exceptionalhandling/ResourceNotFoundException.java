package com.example.footballmatch.exceptionalhandling;

public class ResourceNotFoundException extends Exception
{
    private static final long serialVersionUID = -357212891829813434L;

    public ResourceNotFoundException()
    {
        super();
    }

    public ResourceNotFoundException(final String message)
    {
        super(message);
    }
}
