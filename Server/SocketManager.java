/* ---------------------- server/SocketManager.java ---------------------- */
package server;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * 클라이언트 소켓 연결 관리 클래스
 * - ServerSocket으로 접속 대기
 * - BufferedReader/PrintWriter로 메시지 송수신
 */
public class SocketManager {
    private ServerSocket ss;                             // 서버 소켓
    private List<Socket> clients = new ArrayList<>();    // 연결된 클라이언트 소켓
    private Map<Integer, BufferedReader> readers = new HashMap<>();
    private Map<Integer, PrintWriter> writers = new HashMap<>();

    /**
     * 지정 포트로 ServerSocket 초기화
     */
    public SocketManager(int port) throws IOException {
        ss = new ServerSocket(port);
    }

    /**
     * 두 클라이언트 연결 수락 및 스트림 초기화
     */
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

    /**
     * 모든 클라이언트에게 메시지 전송
     */
    public void broadcast(String msg) {
        for (PrintWriter pw : writers.values()) {
            pw.println(msg);
        }
    }

    /**
     * 특정 플레이어에게만 메시지 전송
     */
    public void sendTo(int player, String msg) {
        writers.get(player).println(msg);
    }

    /**
     * 특정 플레이어로부터 메시지 읽기
     */
    public String receiveFrom(int player) throws IOException {
        return readers.get(player).readLine();
    }

    /**
     * 모든 소켓 자원 닫기
     */
    public void stop() throws IOException {
        for (Socket s : clients) s.close();
        ss.close();
    }
}