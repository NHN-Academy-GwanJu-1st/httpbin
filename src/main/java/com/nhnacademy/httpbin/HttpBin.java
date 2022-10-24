package com.nhnacademy.httpbin;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class HttpBin implements Runnable{

    private static final int PORT = 12345;
    private static final String HOST = "test-vm.com";
    private static final String USER_AGENT = "User-Agent: curl/7.68.0";
    private static final String ACCEPT = "Accept: */*";


    private String url;
    private String method;
    private boolean verbos;

    private Socket socket;
    public HttpBin(Socket socket) {
        this.socket = socket;
        this.method = "GET";
        this.verbos = false;
    }

    public static void main(String[] args) throws IOException {

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server Start");

            while (true) {
                HttpBin myServer = new HttpBin(serverSocket.accept());
                System.out.println("Server Connect Success!");

                Thread thread = new Thread(myServer);
                thread.start();

            }
        } catch (IOException e) {
            throw new RuntimeException("Server Connect Fail!");
        }
    }

    @Override
    public void run() {

        /* TODO ## 서버를 127.0.0.1 로 열어줘서
        *   curl localhost는 정상 작동함.
        *   하지만  test-vm을 할경우 다른 ip로 시도되어짐
        *   hosts 파일 설정을 바꿔도 소용이 없음....*/

    }

    public void run(HttpBin httpBin, String[] args) throws IOException {

        httpBin.optionCheck(args);
        /* curl -v http://test-vm/ip */

        URL url = new URL(httpBin.url);


        Socket socket = new Socket();

        SocketAddress address = new InetSocketAddress(url.getHost(), PORT);

        System.out.println("url getHOst: " + url.getHost());
        System.out.println("url getPath "  +url.getPath());

        /* 접속 IP 주소 */
        String clientIp = InetAddress.getLocalHost().getHostAddress();
        System.out.println(clientIp);
        socket.connect(address);

        StringBuilder outBuilder = new StringBuilder();
        outBuilder.append(
                httpBin.method + " /" + url.getPath() + " HTTP/1.1" + "\n" +
                "Host: " + url.getHost() + "\n" +
                USER_AGENT + "\n" +
                ACCEPT + "\n"
        );

        if (httpBin.verbos) {
            System.out.println(outBuilder);
        }

        PrintWriter out = new PrintWriter(socket.getOutputStream());

        out.println(outBuilder);
        out.println();
        out.flush();

        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = input.readLine()) != null) {

            if (!httpBin.verbos) {
                if (!line.equals("{")) {
                    continue;
                } else {
                    httpBin.verbos = true;
                }
            }
            sb.append(line + "\n");
        }


    }

    public void optionCheck(String[] params) {

        for (int i = 0; i < params.length; i++) {
            if (params[i].contains("http")) {
                this.url = params[i];
            } else if (params[i].equals("-v")) {
                this.verbos = true;
            } else if (params[i].equals("-X")) {
                this.method = params[i + 1];
            }
        }
    }

}
