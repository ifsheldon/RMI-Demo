package myrmi.server;

import myrmi.Remote;

import java.lang.reflect.Proxy;

public class Util
{


    public static Remote createStub(RemoteObjectRef ref)
    {
        //TODO: finish
        ClassLoader cl = Remote.class.getClassLoader();
        try
        {
            return (Remote) Proxy.newProxyInstance(cl, new Class<?>[]{cl.loadClass(ref.getInterfaceName())}, new StubInvocationHandler(ref));
        }catch (ClassNotFoundException cnfe)
        {
            cnfe.printStackTrace();
            return null;
        }
    }


}
