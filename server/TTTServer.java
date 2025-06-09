// TTTServer.java
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TTTServer {
    private ServerSocket serverSocket;
    private Socket waitingClient = null;
    private ServerConsole console;
    private int nextSessionId = 1;

    public TTTServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        console = new ServerConsole();
        System.out.println("서버 시작: 포트 " + port);
        acceptClients();
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
        int port = args.length>0?Integer.parseInt(args[0]):5000;
        try { new TTTServer(port); } catch (IOException e) { e.printStackTrace(); }
    }
}
