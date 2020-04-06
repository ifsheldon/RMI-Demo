package myrmi.server;

import myrmi.Remote;
import myrmi.exception.RemoteException;
import myrmi.intermediate.Message;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SkeletonReqHandler extends Thread
{
    private Socket socket;
    private Remote obj;
    private int objectKey;
    private Logger srhLogger = Logger.getLogger("SkeletonReqHandlerLogger");

    public SkeletonReqHandler(Socket socket, Remote remoteObj, int objectKey)
    {
        this.socket = socket;
        this.obj = remoteObj;
        this.objectKey = objectKey;
    }

    private Method getMethod(Class<?> claz, String methodName, Object[] args) throws RemoteException, NoSuchMethodException
    {
        if (args == null || args.length == 0)// if the desired method has no arguments.
            return claz.getMethod(methodName, null);

        //else, filter out methods that have different names and different numbers of arguments
        List<Method> candidates = Arrays.stream(claz.getMethods())
                .filter(method -> methodName.equals(method.getName()))
                .filter(method -> method.getParameterCount() == args.length).collect(Collectors.toList());

        if (candidates.size() == 0)
            throw new NoSuchMethodException();
        else
        {
            ArrayList<Method> matchedMethods = new ArrayList<>();
            for (Method m : candidates)
            {
                Class<?>[] types = m.getParameterTypes();
                boolean match = true;
                //iterate over all parameter types and check whether argument match each type
                for (int i = 0; i < types.length; i++)
                {
                    Class<?> argITypeOfMethod = types[i];
                    //specially handle the cases of primitives
                    if(argITypeOfMethod.isPrimitive())
                    {
                        if(argITypeOfMethod.equals(int.class))
                            argITypeOfMethod = Integer.class;
                        else if(argITypeOfMethod.equals(double.class))
                            argITypeOfMethod = Double.class;
                        else if(argITypeOfMethod.equals(boolean.class))
                            argITypeOfMethod = Boolean.class;
                        else if(argITypeOfMethod.equals(byte.class))
                            argITypeOfMethod = Byte.class;
                        else if(argITypeOfMethod.equals(float.class))
                            argITypeOfMethod = Float.class;
                        else if(argITypeOfMethod.equals(short.class))
                            argITypeOfMethod = Short.class;
                        else if(argITypeOfMethod.equals(char.class))
                            argITypeOfMethod = Character.class;
                        else if(argITypeOfMethod.equals(long.class))
                            argITypeOfMethod = Long.class;
                        else
                            srhLogger.severe("Should not enter this branch");
                    }

                    if (!argITypeOfMethod.isInstance(args[i]))
                    {
                        match = false;
                        break;
                    }
                }
                if (match)
                    matchedMethods.add(m);
            }
            if (matchedMethods.size() == 0)
            {
                throw new NoSuchMethodException();
            } else if (matchedMethods.size() > 1)
            {
                StringBuilder sb = new StringBuilder();
                sb.append("Ambiguity Exception: too many matched methods: \n");
                matchedMethods.forEach(m -> sb.append("   ").append(m.toString()).append("\n"));
                throw new RemoteException(sb.toString());
            } else
                return matchedMethods.get(0);
        }
    }

    @Override
    public void run()
    {
        /*TODO: implement method here
         * You need to:
         * 1. handle requests from stub, receive invocation arguments
         * 2. get result by calling the real object, and handle different cases (non-void method, void method, method throws exception, exception in invocation process)
         * Hint: you can use a int to represent the cases: -1 invocation error, 0 exception thrown,
         * 1 void method, 2 non-void method
         *
         *  */
        try
        {
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            Message request = (Message) ois.readObject(); //get request msg
            Message reply = new Message(this.objectKey, request.getMethodName(), 0);
            if (request.getObjectKey() != this.objectKey) // mismatched object key
            {
                RemoteException re = new RemoteException("Object Key not match");
                srhLogger.warning(String.format("Object Key not match:\n    Request IP:%s\n    RequestObjectKey:%d\n    CurrentObjectKey:%d\n",
                        socket.getInetAddress(), request.getObjectKey(), this.objectKey));
                reply.setResult(re, Message.ResultStatus.ExceptionThrown);
            } else
            {
                try
                {
                    Method requestedMethod = getMethod(obj.getClass(), request.getMethodName(), request.getArgs());
                    boolean originAccessibility = requestedMethod.isAccessible();
                    requestedMethod.setAccessible(true); // in case of private methods
                    try
                    {
                        Object returnVal = requestedMethod.invoke(obj, request.getArgs());
                        // all things gone fine
                        reply.setResult(returnVal, Message.ResultStatus.Success);
                    } catch (IllegalAccessException e) // should not happen since already set accessible
                    {
                        srhLogger.severe(String.format("Should Not Happen:\n%s", e.getMessage()));
                        reply.setResult(-1, Message.ResultStatus.ServerSideError);
                    } catch (InvocationTargetException e) // underlying method threw an exception
                    {
                        Throwable cause = e.getCause();
                        reply.setResult(cause, Message.ResultStatus.ExceptionThrown);
                    } finally
                    {
                        requestedMethod.setAccessible(originAccessibility);
                    }
                } catch (NoSuchMethodException nse)
                {
                    reply.setResult(nse, Message.ResultStatus.InvocationError);
                }
            }

            //send reply back to client
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(reply);
            oos.flush();
            oos.close();

        } catch (IOException | ClassNotFoundException e) // if at server side IOException or not found the class of Message
        {
            srhLogger.severe(e.getMessage());
        } finally
        {
            if (!socket.isClosed())
            {
                try
                {
                    socket.close();
                } catch (IOException e)
                {
                    srhLogger.warning(e.getMessage());
                }
            }
        }
    }

}
