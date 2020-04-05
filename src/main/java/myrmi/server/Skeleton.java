package myrmi.server;

import myrmi.Remote;
import myrmi.intermediate.RemoteObjectRef;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

// Improvement TODO: use thread pool
public class Skeleton extends Thread
{
    static final int BACKLOG = 5;
    private Logger logger = Logger.getLogger("SkeletonLogger");
    private Remote remoteObj;

    private String host;
    private int port;
    private int objectKey;

    public int getPort()
    {
        return port;
    }

    public Skeleton(Remote remoteObj, RemoteObjectRef ref)
    {
        this(remoteObj, ref.getHost(), ref.getPort(), ref.getObjectKey());
    }

    public Skeleton(Remote remoteObj, String host, int port, int objectKey)
    {
        super();
        this.remoteObj = remoteObj;
        this.host = host;
        this.port = port;
        this.objectKey = objectKey;
        this.setDaemon(false);
    }

    @Override
    public void run()
    {
        /*TODO: implement method here
         * You need to:
         * 1. create a server socket to listen for incoming connections
         * 2. use a handler thread to process each request (SkeletonReqHandler)
         *  */
        try
        {
            ServerSocket listenSocket = new ServerSocket(port);
            Socket socket;
            while ((socket = listenSocket.accept()) != null)
            {
                SkeletonReqHandler requestHandler = new SkeletonReqHandler(socket, remoteObj, objectKey);
                requestHandler.start();
            }
            logger.info("server socket listening finished");
        } catch (IOException ioe)
        {
            logger.warning(ioe.getMessage());
        }

    }
}
