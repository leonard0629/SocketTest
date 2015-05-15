package com.leonard.SocketTest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.WebView;
import com.leonard.SocketTest.socket.WebSocketFactory;
import com.leonard.SocketTest.socket.WebSocketServerThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MyActivity extends Activity {

    private WebView webViewWS;

    private Thread serverThread = null;
    private ServerSocket serverSocket;
    private Handler updateConversationHandler;

    private static final int SERVER_PORT = 9999;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initView();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    protected void initView() {
        webViewWS = (WebView) findViewById(R.id.webViewWS);

        serverThread = new Thread(new ServerThread());
        serverThread.start();

        updateConversationHandler = new Handler();

        String url = "file:///android_asset/www/index.html";
        webViewWS.loadUrl(url);
        webViewWS.getSettings().setJavaScriptEnabled(true);
        webViewWS.addJavascriptInterface(new WebSocketFactory(mHandler, webViewWS), "WebSocketFactory");
    }

    private class ServerThread implements Runnable {

        public void run() {
            Socket socket = null;
            try {
                serverSocket = new ServerSocket(SERVER_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    socket = serverSocket.accept();
                    CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class CommunicationThread implements Runnable {

        private Socket clientSocket;

        private BufferedReader input;

        public CommunicationThread(Socket clientSocket) {

            this.clientSocket = clientSocket;

            try {

                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {

            while (!Thread.currentThread().isInterrupted()) {

                try {

                    String read = input.readLine();

                    updateConversationHandler.post(new updateUIThread(read));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class updateUIThread implements Runnable {
        private String msg;

        public updateUIThread(String str) {
            this.msg = str;
        }

        @Override
        public void run() {
            System.out.println("msg:" + msg);
        }
    }
}
