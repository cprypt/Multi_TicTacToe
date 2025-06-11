// TTTServer.java
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class TTTServer {
    private ServerSocket serverSocket;
    private Socket waitingClient = null;
    private ServerConsole console;
    private int nextSessionId = 1;

    public TTTServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        SwingUtilities.invokeLater(() -> console = new ServerConsole());
        System.out.println("멀티플레이어 틱택토 서버 시작: 포트 " + port);
        new Thread(() -> acceptClients()).start();
    }

    private void acceptClients() {
        while (true) {
            try {
                Socket sock = serverSocket.accept();
                System.out.println("클라이언트 접속: " + sock.getRemoteSocketAddress());
                console.addClient(sock);

                synchronized (this) {
                    if (waitingClient == null) {
                        waitingClient = sock;
                        System.out.println("상대 연결 대기 중...");
                    } else {
                        Socket c1 = waitingClient;
                        Socket c2 = sock;
                        waitingClient = null;

                        console.addSession(nextSessionId, c1, c2);
                        Thread t = new Thread(new GameSession(nextSessionId, c1, c2, console),
                                              "Session-" + nextSessionId);
                        t.start();
                        System.out.println("세션 " + nextSessionId + " 시작.");
                        nextSessionId++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        try { serverSocket.close(); } catch (IOException ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String portStr = JOptionPane.showInputDialog(
                null, "서버 Port:", "5000");
            if (portStr == null) System.exit(0);
            try {
                int port = Integer.parseInt(portStr.trim());
                new TTTServer(port);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    null, "잘못된 Port: " + ex.getMessage(),
                    "연결 에러", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
