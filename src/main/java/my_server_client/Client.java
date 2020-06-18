package my_server_client;

import myrmi.registry.LocateRegistry;
import myrmi.registry.Registry;

import java.io.IOException;

import java.util.Calendar;

public class Client
{
    public static void main(String[] args)
    {
        try
        {
            Registry registry = LocateRegistry.getRegistry("localhost", 2021);
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
            e.printStackTrace();
        }
    }
}
