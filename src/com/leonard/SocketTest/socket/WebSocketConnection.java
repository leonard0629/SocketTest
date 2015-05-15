package com.leonard.SocketTest.socket;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by leonard on 2015/5/15.
 */
public class WebSocketConnection implements Runnable {
    private Socket client;
    private BufferedReader reader;
    private boolean isFinished = false;

    public WebSocketConnection(){}
    public WebSocketConnection(Socket client) {
        this.client = client;
        try {
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String msg;
        while (!isFinished) {
            try {
                if((msg = reader.readLine())!= null) {
                    //当客户端发送的信息为：exit时，关闭连接
                    Log.v("test", msg);

                }
            } catch (IOException e) {
                System.out.println("close");
                isFinished = true;
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
