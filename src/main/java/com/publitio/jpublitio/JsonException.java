package com.publitio.jpublitio;

/**
 * This exception is thrown when parsing the response JSON fails.
 * It might mean either that your endpoint path is wrong, or there
 * was an internal server error.
 */
public class JsonException extends Exception
{
    public static final long serialVersionUID = 53442;

    public JsonException(String message)
    {
        super(message);
    }
}
