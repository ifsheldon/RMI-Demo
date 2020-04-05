package myrmi.server;

import myrmi.exception.RemoteException;
import myrmi.registry.Registry;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;

public class StubInvocationHandler implements InvocationHandler, Serializable
{
    private String host;
    private int port;
    private int objectKey;

    public StubInvocationHandler(String host, int port, int objectKey)
    {
        this.host = host;
        this.port = port;
        this.objectKey = objectKey;
        System.out.printf("Stub created to %s:%d, object key = %d\n", host, port, objectKey);
    }

    public StubInvocationHandler(RemoteObjectRef ref)
    {
        this(ref.getHost(), ref.getPort(), ref.getObjectKey());
    }

    @Override
    public Object invoke(Object callingProxy, Method method, Object[] args) throws RemoteException, IOException, ClassNotFoundException, Throwable
    {
        /*TODO: implement stub proxy invocation handler here
         *  You need to do:
         * 1. connect to remote skeleton, send method and arguments
         * 2. get result back and return to caller transparently
         * */
        Socket socket = new Socket(host, port);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        Message request = new Message(objectKey, method.getName(), args);
        oos.writeObject(request);
        ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
        Message reply = (Message) oin.readObject();
        switch (reply.getStatus())
        {
            case None:
                throw new RemoteException("None replied");
            case ExceptionThrown:
            case InvocationError:
                throw (Throwable) reply.result;
            case ServerSideError:
                throw new RemoteException("ServerSideError");
            case Success:
                return reply.result;
            default:
                return null;
        }
    }

}
