/* ---------------------- server/SocketManager.java ---------------------- */
package Server;

import java.net.*;
import java.io.*;
import java.util.*;

public class SocketManager {
    private ServerSocket ss;
    private List<Socket> clients = new ArrayList<>();
    private Map<Integer, BufferedReader> readers = new HashMap<>();
    private Map<Integer, PrintWriter> writers = new HashMap<>();

    public SocketManager(int port) throws IOException {
        ss = new ServerSocket(port);
    }

    public void start() throws IOException {
        System.out.println("Waiting for 2 clients...");
        for (int i = 1; i <= 2; i++) {
            Socket s = ss.accept();
            clients.add(s);
            readers.put(i, new BufferedReader(new InputStreamReader(s.getInputStream())));
            writers.put(i, new PrintWriter(s.getOutputStream(), true));
            System.out.println("Client " + i + " connected.");
        }
    }

    public void broadcast(String msg) {
        for (PrintWriter pw : writers.values()) {
            pw.println(msg);
        }
    }

    public void sendTo(int player, String msg) {
        writers.get(player).println(msg);
    }

    public String receiveFrom(int player) throws IOException {
        return readers.get(player).readLine();
    }

    public void stop() throws IOException {
        for (Socket s : clients) s.close();
        ss.close();
    }
}