package my_server_client;

import myrmi.exception.RemoteException;
import myrmi.registry.LocateRegistry;
import myrmi.registry.Registry;




public class ServerRunner
{
    public static void main(String[] args)
    {
        try
        {
            Registry registry = LocateRegistry.createRegistry(2021);
            RemoteFileServer fileServer = new FileServer();
            registry.rebind("remote_file_server", fileServer);
            System.out.println("RMI Registry started\nfile server ready");
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }
}
