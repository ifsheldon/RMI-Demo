package my_server_client;

import myrmi.Remote;

import java.io.IOException;


public interface RemoteFileServer extends Remote
{
    String read(String filename) throws IOException;

    void edit(String filename, String content, boolean append) throws IOException;

    long size(String filename) throws IOException;

    long lastModified(String filename) throws IOException;

    long lastAccessed(String filename) throws IOException;

    boolean create(String fileName, boolean replaceExisted) throws IOException;

    boolean delete(String fileName) throws IOException;

    void copy(String sourceFileName, String destFileName, boolean replaceExisted) throws IOException;

    void move(String sourceFileName, String destFileName, boolean replaceExisted) throws IOException;

    void rename(String original, String newName, boolean replaceExisted) throws IOException;
}
