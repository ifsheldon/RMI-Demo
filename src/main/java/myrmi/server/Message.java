package myrmi.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Message implements Serializable
{
    public enum ResultStatus
    {
        None, ExceptionThrown, InvocationError, Success, ServerSideError
    }

    int objectKey;//not sure whether it is needed
    String methodName;
    Class<?>[] argTypes;
    Object[] args;
    Object result;
    ResultStatus status = ResultStatus.None;

    public Message(@NotNull int objectKey, @NotNull String methodName, @Nullable Object... args)
    {
        this.objectKey = objectKey;
        this.methodName = methodName;
        this.args = args;
        if (args == null || args.length == 0)
        {
            argTypes = null;
        } else
        {
            List<Class<?>> argList = Arrays.stream(args).map(Object::getClass).collect(Collectors.toList());
            argTypes = new Class<?>[argList.size()];
            argList.toArray(argTypes);
        }
    }

    public Object getResult()
    {
        return result;
    }

    public ResultStatus getStatus()
    {
        return status;
    }

    public void setResult(@Nullable Object result, @NotNull ResultStatus status)
    {
        this.result = result;
        this.status = status;
    }
}
