/* ---------------------- client/SocketClient.java ---------------------- */
package client;

import java.io.*;
import java.net.*;

/**
 * 서버와의 소켓 연결 클래스
 * - PrintWriter/BufferedReader로 메시지 송수신
 */
public class SocketClient {
    private Socket s;
    private BufferedReader br;
    private PrintWriter pw;

    public SocketClient(String host, int port) throws IOException {
        s = new Socket(host, port);
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        pw = new PrintWriter(s.getOutputStream(), true);
    }

    public void sendMessage(String msg) { pw.println(msg); }
    public String receiveMessage() throws IOException { return br.readLine(); }
    public void disconnect() throws IOException { s.close(); }
}
