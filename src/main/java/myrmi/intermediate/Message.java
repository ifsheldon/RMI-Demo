package myrmi.intermediate;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.Serializable;

public class Message implements Serializable
{
    public enum ResultStatus
    {
        None, ExceptionThrown, InvocationError, Success, ServerSideError
    }

    private int objectKey;//not sure whether it is needed
    private String methodName;
    private Object[] args;
    private Object result;
    private ResultStatus status = ResultStatus.None;

    public Message(@NotNull int objectKey, @NotNull String methodName, @Nullable Object... args)
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

    public void setResult(@Nullable Object result, @NotNull ResultStatus status)
    {
        this.result = result;
        this.status = status;
    }
}
