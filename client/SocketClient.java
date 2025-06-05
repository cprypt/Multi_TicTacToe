/* ---------------------- client/SocketClient.java ---------------------- */
package client;

import java.io.*;
import java.net.*;

/**
 * 서버와의 소켓 연결 클래스
 * - PrintWriter/BufferedReader로 메시지 송수신
 */
public class SocketClient {
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    public SocketClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printWriter = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendMessage(String msg) { printWriter.println(msg); }
    public String receiveMessage() throws IOException { return bufferedReader.readLine(); }
    public void disconnect() throws IOException { socket.close(); }
}