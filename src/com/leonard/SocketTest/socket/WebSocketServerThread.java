package com.leonard.SocketTest.socket;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by leonard on 2015/5/15.
 */
public class WebSocketServerThread extends Thread {

    private final static String TAG = "WebSocketServerThread";
    private final static int SERVER_PORT = 9999;

    private ServerSocket server;
    private List<Socket> clients = new ArrayList<Socket>();
    private ExecutorService mExecutorService;
    private boolean isInterrupt = false;

    @Override
    public void run() {
        try {
            server = new ServerSocket(SERVER_PORT);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        mExecutorService = Executors.newCachedThreadPool();  //创建一个线程池
        Log.v(TAG, "服务器已启动...");
        Socket client = null;
        while(!isInterrupt) {
            try {
                client = server.accept();
                //把客户端放入客户端集合中
                clients.add(client);
                mExecutorService.execute(new WebSocketConnection(client)); //启动一个新的线程来处理连接
            }catch ( IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void interrupt() {
        isInterrupt = true;

        if(server != null) {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}