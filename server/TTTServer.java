package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 다중 세션을 지원하는 TicTacToe 서버
 * 하나의 ServerSocket으로 무한 대기
 * 두 개씩 묶어 GameSession 쓰레드 생성
 * ServerConsole로 상태 표시
 */
public class TTTServer {
    private ServerSocket serverSocket;
    private Socket waitingClient = null;             // 페어 대기 클라이언트
    private ServerConsole console;                   // 서버 관리 GUI 콘솔
    private int nextSessionId = 1;                   // 세션 고유 ID

    public TTTServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        console = new ServerConsole();               // GUI 콘솔 띄우기
        System.out.println("멀티플레이어 틱택토 서버: 포트 " + port);
        acceptClients();
    }

    private void acceptClients() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("클라이언트 접속됨: " + clientSocket.getRemoteSocketAddress());
                console.addWaiting(clientSocket);    // 대기 목록에 추가

                synchronized (this) {
                    if (waitingClient == null) {
                        // 첫 번째 플레이어 대기
                        waitingClient = clientSocket;
                        System.out.println("상대 클라이언트를 기다리는 중...");
                    } else {
                        // 두 번째 플레이어와 매칭
                        Socket client1 = waitingClient;
                        Socket client2 = clientSocket;
                        waitingClient = null;         // 대기 큐 비우기
                        console.removeWaiting(client1);

                        int sessionId = nextSessionId++;
                        console.addSession(sessionId, client1, client2); // 세션 목록에 추가

                        // GameSession 생성 (세션 ID, 두 소켓, 콘솔 전달)
                        GameSession session = new GameSession(sessionId, client1, client2, console);
                        new Thread(session, "Session-" + sessionId).start();
                        System.out.println("새로운 세션 " + sessionId + " 시작: "
                            + client1.getRemoteSocketAddress() + " vs " + client2.getRemoteSocketAddress());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

        // 서버 종료 시 ServerSocket 닫기
        try {
            serverSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = args.length >= 1 ? Integer.parseInt(args[0]) : 5000;
        try {
            new TTTServer(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}