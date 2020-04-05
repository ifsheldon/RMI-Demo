package myrmi.server;

import myrmi.Remote;
import myrmi.exception.RemoteException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class UnicastRemoteObject implements Remote, java.io.Serializable {
    int port;

    protected UnicastRemoteObject() throws RemoteException {
        this(0);
    }

    protected UnicastRemoteObject(int port) throws RemoteException {
        this.port = port;
        exportObject(this, port);
    }

    public static Remote exportObject(Remote obj) throws RemoteException {
        return exportObject(obj, 0);
    }

    public static Remote exportObject(Remote obj, int port) throws RemoteException {


        return exportObject(obj, "127.0.0.1", port);
    }

    /**
     * create skeleton on given host:port
     * returns a stub
     **/
    public static Remote exportObject(Remote obj, String host, int port) throws RemoteException {
        //TODO: finish here
        throw new NotImplementedException();
    }
}