/* ---------------------- Client/SocketClient.java ---------------------- */
package Client;

import java.io.*;
import java.net.*;

public class SocketClient {
    private Socket s;
    private BufferedReader br;
    private PrintWriter pw;

    public SocketClient(String host, int port) throws IOException {
        s = new Socket(host, port);
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        pw = new PrintWriter(s.getOutputStream(), true);
    }

    public void sendMessage(String msg) {
        pw.println(msg);
    }

    public String receiveMessage() throws IOException {
        return br.readLine();
    }

    public void disconnect() throws IOException {
        s.close();
    }
}