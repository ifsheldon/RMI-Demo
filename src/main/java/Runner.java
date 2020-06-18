import my_server_client.FileServer;
import my_server_client.RemoteFileServer;
import myrmi.exception.RemoteException;
import myrmi.registry.LocateRegistry;
import myrmi.registry.Registry;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

public class Runner
{
    public static void main(String[] args)
    {
        int option = Integer.parseInt(args[0]);
        String ip = args[1];
        int port = Integer.parseInt(args[2]);
        switch (option)
        {
            case 0:
                runRegistry(ip, port);
                break;
            case 1:
                runFileServer(ip, port);
                break;
            case 2:
                runClient(ip, port);
                break;
            default:
                System.err.println("Error, option not found");
        }
    }

    private static void runClient(String registryIP, int registryPort)
    {
        Logger log = Logger.getLogger("ClientLog");
        try
        {
            Registry registry = LocateRegistry.getRegistry(registryIP, registryPort);
            RemoteFileServer remoteFileServer = (RemoteFileServer) registry.lookup("remote_file_server");
            String format = "File Client: %s\n";
            System.out.printf(format, "creating file test.txt");
            remoteFileServer.create("test.txt", true);
            System.out.printf(format, "editing(appending) file test.txt");
            remoteFileServer.edit("test.txt", Calendar.getInstance().getTime().toString(), true);
            System.out.printf(format, "renaming file test.txt to test2.txt");
            remoteFileServer.rename("test.txt", "test2.txt", true);
            System.out.printf(format, "copying file test2.txt to test1.txt");
            remoteFileServer.copy("test2.txt", "test1.txt", true);
            System.out.printf(format, "moving file test2.txt to test3.txt");
            remoteFileServer.move("test2.txt", "test3.txt", true);
            System.out.printf(format, "reading the content of file test3.txt :");
            System.out.print(remoteFileServer.read("test3.txt"));
            System.out.printf(format,
                    String.format("reading the lastAccessed and lastModified of file test3.txt\n lastAccessed: %d\n lastModified: %d",
                            remoteFileServer.lastAccessed("test3.txt"),
                            remoteFileServer.lastModified("test3.txt")));
            System.out.printf(format, String.format("size of test3.txt is %d bytes", remoteFileServer.size("test3.txt")));
        } catch (IOException | myrmi.exception.NotBoundException e)
        {
            log.warning(e.getMessage());
        }
    }

    private static void runFileServer(String registryIP, int registryPort)
    {
        Logger log = Logger.getLogger("FileServerLog");
        try
        {
            Registry registry = LocateRegistry.getRegistry(registryIP, registryPort);
            RemoteFileServer fileServer = new FileServer();
            registry.rebind("remote_file_server", fileServer);
            System.out.println("RMI Registry started\nfile server ready");
        } catch (RemoteException e)
        {
            log.warning(e.getMessage());
        }
    }

    private static void runRegistry(String ip, int port)
    {
        Logger log = Logger.getLogger("RegistryLog");
        try
        {
            Registry registry = LocateRegistry.createRegistry(ip, port);
            while (true)
            {
                try
                {
                    Thread.sleep(50);
                } catch (InterruptedException e)
                {
                    break;
                }
            }
        } catch (RemoteException e)
        {
            log.warning(e.getMessage());
        }
    }
}
