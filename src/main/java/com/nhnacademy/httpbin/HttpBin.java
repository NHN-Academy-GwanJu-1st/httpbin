package com.nhnacademy.httpbin;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class HttpBin implements Runnable{

    private static final int PORT = 80;

    private static final String httpMethod = "GET";

    private Socket socket;


    public HttpBin(Socket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("server start");

            while (true) {
                HttpBin myServer = new HttpBin(serverSocket.accept());
                System.out.println("Server Connect Success!");

                Thread thread = new Thread(myServer);
                thread.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static BufferedReader input;
    static PrintWriter out;
    static BufferedOutputStream dataOut;

    @Override
    public void run() {

        try {
            StringBuilder sb;

//            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            PrintWriter out = new PrintWriter(socket.getOutputStream());
//            BufferedOutputStream dataOut = new BufferedOutputStream(socket.getOutputStream());
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            dataOut = new BufferedOutputStream(socket.getOutputStream());

            String requestHeader = input.readLine();
            System.out.println(requestHeader);
            StringTokenizer st = new StringTokenizer(requestHeader);

            /* 요청 HTTP Method  ex: GET / (url parameter) HTTP/1.1 */
            String httpMethod = st.nextToken().toUpperCase();
            System.out.println(httpMethod);

            /* url Parameter ex: localhost:12345/( urlParameter ) */
            String urlParameter = st.nextToken().toLowerCase();
            System.out.println("requestParameter : " + urlParameter);
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
