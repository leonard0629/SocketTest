package com.leonard.SocketTest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.WebView;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.Protocol;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.leonard.SocketTest.socket.WebSocketFactory;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyActivity extends Activity {

    private WebView webViewWS;

    private Thread serverThread = null;
    private Thread clientThread = null;
    private WebSocketServer serverSocket;
    private Handler updateConversationHandler;

    private AsyncHttpServer server;

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

//        clientThread = new Thread(new ClientThread());
//        clientThread.start();

        updateConversationHandler = new Handler();

        String url = "file:///android_asset/www/index.html";
        webViewWS.loadUrl(url);
        webViewWS.getSettings().setJavaScriptEnabled(true);
        webViewWS.addJavascriptInterface(new WebSocketFactory(webViewWS), "WebSocketFactory");

    }

    private class ServerThread implements Runnable {

        public void run() {
            serverSocket = new WebSocketServer(new InetSocketAddress(SERVER_PORT)) {
                @Override
                public void onOpen(org.java_websocket.WebSocket webSocket, ClientHandshake clientHandshake) {
                    System.out.println("onOpen");
                }

                @Override
                public void onClose(org.java_websocket.WebSocket webSocket, int i, String s, boolean b) {
                    System.out.println("onClose");
                }

                @Override
                public void onMessage(org.java_websocket.WebSocket webSocket, String s) {
                    System.out.println("onMessage, msg:" + s);
                }

                @Override
                public void onError(org.java_websocket.WebSocket webSocket, Exception e) {
                    System.out.println("onError");
                }
            };
            serverSocket.start();
//            while (!Thread.currentThread().isInterrupted()) {
//                try {
//                    Socket socket = serverSocket.accept();
//                    CommunicationThread commThread = new CommunicationThread(socket);
//                    new Thread(commThread).start();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
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

    private class ClientThread implements Runnable {
        @Override
        public void run() {
            AsyncHttpClient.getDefaultInstance().websocket("ws://127.0.0.1:9999", Protocol.HTTP_1_1.toString(), new AsyncHttpClient.WebSocketConnectCallback() {
                @Override
                public void onCompleted(Exception ex, WebSocket webSocket) {
                    webSocket.send("a string");
                }
            });
        }
    }
}
