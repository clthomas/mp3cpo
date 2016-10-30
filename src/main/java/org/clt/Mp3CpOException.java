package org.clt;

/**
 * Created by clt on 10/26/16.
 */
public class Mp3CpOException extends RuntimeException {

    public Mp3CpOException(final String message, final Object ... args)
    {
        super(String.format(message, args));
    }

    public Mp3CpOException(final String message, final Exception e, final Object ... args)
    {
        super(String.format(message, args), e);
    }
}
