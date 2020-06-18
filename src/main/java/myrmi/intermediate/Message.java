package myrmi.intermediate;


import java.io.Serializable;

/**
 * Attribute class
 * For exchanging requests and replies via serialization and network
 */
public class Message implements Serializable
{
    /**
     * Indicating different status
     */
    public enum ResultStatus
    {
        None, ExceptionThrown, InvocationError, Success, ServerSideError
    }

    private final int objectKey;//not sure whether it is needed
    private final String methodName;
    private final Object[] args;
    private Object result;
    private ResultStatus status = ResultStatus.None;

    public Message(int objectKey, String methodName, Object... args)
    {
        this.objectKey = objectKey;
        this.methodName = methodName;
        this.args = args;
    }

    public Object getResult()
    {
        return result;
    }

    public ResultStatus getStatus()
    {
        return status;
    }

    public int getObjectKey()
    {
        return objectKey;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public Object[] getArgs()
    {
        return args;
    }

    public void setResult(Object result, ResultStatus status)
    {
        this.result = result;
        this.status = status;
    }
}
