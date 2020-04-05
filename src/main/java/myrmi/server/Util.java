package myrmi.server;

import myrmi.Remote;

import java.lang.reflect.Proxy;

public class Util
{


    public static Remote createStub(RemoteObjectRef ref)
    {
        //TODO: finish
        return (Remote) Proxy.newProxyInstance(Remote.class.getClassLoader(), new Class<?>[]{Remote.class}, new StubInvocationHandler(ref));
    }


}
