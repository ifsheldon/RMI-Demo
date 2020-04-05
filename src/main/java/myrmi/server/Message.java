package myrmi.server;

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

    public Message(int objectKey, String methodName, Object... args)
    {
        this.objectKey = objectKey;
        this.methodName = methodName;
        this.args = args;
        List<Class<?>> argList = Arrays.stream(args).map(Object::getClass).collect(Collectors.toList());
        argTypes = new Class<?>[argList.size()];
        argList.toArray(argTypes);
    }

    public Object getResult()
    {
        return result;
    }

    public ResultStatus getStatus()
    {
        return status;
    }

    public void setResult(Object result, ResultStatus status)
    {
        this.result = result;
        this.status = status;
    }
}
