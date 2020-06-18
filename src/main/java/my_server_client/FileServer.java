package my_server_client;

import myrmi.exception.RemoteException;
import myrmi.server.UnicastRemoteObject;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.util.Calendar;
import java.util.HashMap;

public class FileServer extends UnicastRemoteObject implements RemoteFileServer
{
    private final HashMap<String, Long> accessTable = new HashMap<>();
    private final Calendar calendar = Calendar.getInstance();

    public FileServer() throws RemoteException
    {
    }

    /*
    update accessTable with key = filename and value = local time in milliseconds. if not contains a certain key, then put it into the table
     */
    private void updateTable(String fileName)
    {
        if (accessTable.containsKey(fileName))
            accessTable.replace(fileName, calendar.getTimeInMillis());
        else
            accessTable.put(fileName, calendar.getTimeInMillis());
    }

    /*
    read local file with filename
     */
    @Override
    public String read(String filename) throws IOException
    {
        File f = new File(filename);
        if (!f.exists())
        {
            throw new FileNotFoundException(filename);
        } else
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            StringBuffer sb = new StringBuffer();
            br.lines().forEach(line -> sb.append(line).append("\n"));
            br.close();
            updateTable(filename);
            return sb.toString();
        }
    }

    /*
    Edit local file with filename and content. if append is true, content will be appended to the tail of the document
     */
    @Override
    public void edit(String filename, String content, boolean append) throws IOException
    {
        File f = new File(filename);
        if (!f.exists())
        {
            throw new FileNotFoundException(filename);
        } else
        {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, append)));
            bw.write(content);
            bw.flush();
            bw.close();
            updateTable(filename);
        }
    }

    /*
    get the size in bytes of a file
     */
    @Override
    public long size(String filename) throws IOException
    {
        File f = new File(filename);
        if (!f.exists())
        {
            throw new FileNotFoundException(filename);
        }
        updateTable(filename);
        return f.length();
    }

    /*
    get the last time of modification of a file in milliseconds
     */
    @Override
    public long lastModified(String filename) throws IOException
    {
        File f = new File(filename);
        if (!f.exists())
        {
            throw new FileNotFoundException(filename);
        }
        updateTable(filename);
        return f.lastModified();
    }

    /*
    get the last time of modification of a file in milliseconds
     */
    @Override
    public long lastAccessed(String filename) throws IOException
    {
        File f = new File(filename);
        if (!f.exists())
        {
            throw new FileNotFoundException(filename);
        }
        long t = accessTable.getOrDefault(filename, calendar.getTimeInMillis());
        updateTable(filename);
        return t;
    }

    /*
    create a file with filename.
    if replaceExisted = false and there is a file with the same name, FileAlreadyExistedException will be thrown
     */
    @Override
    public boolean create(String fileName, boolean replaceExisted) throws IOException
    {
        File f = new File(fileName);
        if (f.exists())
        {
            if (!replaceExisted)
            {
                throw new FileAlreadyExistsException(fileName);
            } else
                f.delete();
        }
        updateTable(fileName);
        return f.createNewFile();
    }

    /*
    delete a file with filename, if the file not exist, FileNotFoundException will be thrown
     */
    @Override
    public boolean delete(String fileName) throws IOException
    {
        File f = new File(fileName);
        if (!f.exists())
        {
            throw new FileNotFoundException(fileName);
        }
        boolean deleted = f.delete();
        if (deleted)
            accessTable.remove(fileName);
        return deleted;
    }

    /*
    copy a file to another file.
    if source file not exists, FileNotFoundException will be thrown
    if dest file exist and replaceExisted == false, FileAlreadyExistsException will be thrown
     */
    @Override
    public void copy(String sourceFileName, String destFileName, boolean replaceExisted) throws IOException
    {
        File srcFile = new File(sourceFileName);
        if (!srcFile.exists())
        {
            throw new FileNotFoundException("source not found");
        } else
        {
            File destFile = new File(destFileName);
            if (destFile.exists())
            {
                if (replaceExisted)
                {
                    Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else
                    throw new FileAlreadyExistsException(null, null, "Destination File existed");
            } else
            {
                Files.copy(srcFile.toPath(), destFile.toPath());
            }
            updateTable(sourceFileName);
            updateTable(destFileName);
        }
    }

    /*
    move a file to another file.
    if source file not exists, FileNotFoundException will be thrown
    if dest file exist and replaceExisted == false, FileAlreadyExistsException will be thrown
     */
    @Override
    public void move(String sourceFileName, String destFileName, boolean replaceExisted) throws IOException
    {
        File srcFile = new File(sourceFileName);
        if (!srcFile.exists())
        {
            throw new FileNotFoundException("source not found");
        }
        File destFile = new File(destFileName);
        if (destFile.exists())
        {
            if (replaceExisted)
                Files.move(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            else
                throw new FileAlreadyExistsException(destFileName);
        } else
        {
            Files.move(srcFile.toPath(), destFile.toPath());
        }
        accessTable.remove(sourceFileName);
        updateTable(destFileName);
    }

    /*
    rename a file to another file.
    if source file not exists, FileNotFoundException will be thrown
    if dest file with nawName exist and replaceExisted == false, FileAlreadyExistsException will be thrown
     */
    @Override
    public void rename(String original, String newName, boolean replaceExisted) throws IOException
    {
        move(original, newName, replaceExisted);
    }
}
