package myrmi.exception;

public class RemoteException extends java.io.IOException
{
    public RemoteException()
    {
    }

    public RemoteException(String message)
    {
        super(message);
    }

    public RemoteException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public RemoteException(Throwable cause)
    {
        super(cause);
    }
}
