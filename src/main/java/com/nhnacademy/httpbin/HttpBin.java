package com.nhnacademy.httpbin;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class HttpBin{

    private static final int PORT = 80;
    private static final String HOST = "test-vm.org";
    private static final String USER_AGENT = "User-Agent: curl/7.68.0";
    private static final String ACCEPT = "Accept: */*";


    private String url;
    private String method;
    private boolean verbos;

    public HttpBin() {
        this.verbos = false;
        this.method = "GET";
    }

    public static void main(String[] args) throws IOException {

        if (!args[0].equals("curl")) {
            throw new RuntimeException("잘못된 명령어 입니다.");
        }

        HttpBin httpBin = new HttpBin();

        start(httpBin, args);


    }

    public static void start(HttpBin httpBin, String[] args) throws IOException {

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
