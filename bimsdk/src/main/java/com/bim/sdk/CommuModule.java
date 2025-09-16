package com.bim.sdk;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class CommuModule {

    boolean connected = false;
    private Thread thread;
    private PrintWriter output;
    private OutputStream outputStream;
    private Socket socket;
    private String ipAddress;
//    private Handler outHandler;
    private BimCallback bimCallback;
    private Logger LOGGER;

    private static CommuModule instance = null;

    private CommuModule() { }

    public static synchronized CommuModule self() {
        if (instance == null) {
            instance = new CommuModule();
        }

        return instance;
    }

    public void init() {
        LOGGER = new Logger("BIM", "CommuModule");
    }

    public void connect(String ip, BimCallback bimCallback) {
        this.ipAddress = ip;
        this.bimCallback = bimCallback;

        if (thread == null) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        LOGGER.d( " Connect to Front Service.");
//                    String serverAddress = "localhost"; // 服务器地址
                        int serverPort = 10086; // 服务器端口号
                        socket = new Socket(ipAddress, serverPort);
                        connected = true;
                        if (bimCallback != null) {
                            bimCallback.onResponse(200, null);
                        }
                        output = new PrintWriter(socket.getOutputStream(), true);
                        outputStream = socket.getOutputStream();

                        ProtocolParser protocolParser = new ProtocolParser(socket.getInputStream());
                        ProtocolParser.ParseResult parseResult;
                        while ((parseResult=protocolParser.readParseResult()) != null) {
                            if (bimCallback != null) {
                                bimCallback.onResponse(parseResult.type, parseResult.data);
                            }
                        }
                        LOGGER.e("read parseResult is null.");
                        protocolParser.close();
                        if (bimCallback != null) {
                            bimCallback.onResponse(404, null);
                        }

                        output.close();
                        output = null;
                        outputStream.close();
                        outputStream = null;
                        socket.close();
                    } catch (IOException e) {
                        Log.d("BIM", e.toString());
                        connected = false;
                        if (bimCallback != null) {
                            bimCallback.onResponse(404, null);
                        }
                    }

                    thread = null;
                    LOGGER.d("Thread end.");
                }
            });
            thread.start();
        }
    }

    private boolean sending = false;
    public void sendStr2Server(byte type, String msg) {
        LOGGER.d("---- sendToServer : " + msg + " , sending is " + sending);
        if (!sending) {
            sendData2Server(type, msg.getBytes());
        } else {
            LOGGER.e("Send String failed for busy.");
        }
    }

    public void sendData2Server(byte type, byte[] data) {
        if (outputStream == null) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                sending = true;
                try {
                    byte[] bytes = new byte[4+1+4+data.length+1];
                    bytes[0] = 0x56;
                    bytes[1] = 0x56;
                    bytes[2] = 0x65;
                    bytes[3] = 0x65;
                    bytes[4] = type;
                    byte[] lenArr = Utils.intToBytes(data.length);
                    bytes[5] = lenArr[0];
                    bytes[6] = lenArr[1];
                    bytes[7] = lenArr[2];
                    bytes[8] = lenArr[3];
                    System.arraycopy(data, 0, bytes, 9, data.length);
                    byte sum = 0;
                    for (byte bb : bytes) {
                        sum += bb;
                    }
                    bytes[bytes.length-1] = sum;
                    outputStream.write(bytes);

//                    LOGGER.d("send length is " + bytes.length);
                } catch (IOException e) {
                    LOGGER.e(e.toString());
                    throw new RuntimeException(e);
                }

                sending = false;
            }
        }).start();

//        LOGGER.d("send data : " + Utils.printByteArray(bytes));
    }



//    public void sendBin2Server(byte[] array) {
//        if (!sending) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    sending = true;
//                    if (outputStream != null) {
//                        try {
//                            outputStream.write(array);
//                        } catch (IOException e) {
//                            LOGGER.e(e.toString());
//                        }
//                    }
//                    sending = false;
//                }
//            }).start();
//        } else {
//            LOGGER.e("Send Array failed for busy.");
//        }
//    }

}
