package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 다중 세션을 지원하는 TicTacToe 서버
 * - 하나의 ServerSocket 으로 계속해서 접속 대기
 * - 클라이언트가 접속할 때마다 “대기 큐”를 검사
 *   • 대기 큐가 비어 있으면 → 해당 소켓을 대기 큐에 넣고 “짝을 기다리는 중” 상태
 *   • 대기 큐에 이미 대기 소켓이 있으면 → 두 개를 꺼내서 새로운 GameSession 쓰레드 시작
 */
public class TTTServer {
    private ServerSocket serverSocket;
    // “짝을 기다리는 클라이언트” 소켓 (최대 1개만 대기)
    private Socket waitingClient = null;

    public TTTServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("서버 시작: 포트 " + port);
        acceptClients();
    }

    /**
     * 무한 루프로 클라이언트 연결을 수락하고,
     * 두 명씩 묶어서 GameSession 생성
     */
    private void acceptClients() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("클라이언트 접속됨: " + clientSocket.getRemoteSocketAddress());

                synchronized (this) {
                    // 1) 대기 클라이언트가 없는 경우 → 현재 클라이언트를 대기 상태로 보관
                    if (waitingClient == null) {
                        waitingClient = clientSocket;
                        // (선택) 클라이언트에게 “짝 기다리는 중” 같은 메시지를 보낼 수 있음
                        System.out.println("클라이언트를 대기 큐에 저장. 상대 클라이언트를 기다리는 중...");
                    }
                    // 2) 대기 클라이언트가 이미 있으면 → 두 개 묶어서 Session 생성
                    else {
                        Socket client1 = waitingClient;
                        Socket client2 = clientSocket;
                        waitingClient = null;  // 대기 큐 비우기

                        // 두 클라이언트를 묶어 GameSession 쓰레드 생성
                        GameSession session = new GameSession(client1, client2);
                        Thread sessionThread = new Thread(session);
                        sessionThread.start();
                        System.out.println("새로운 세션 시작: " +
                                           client1.getRemoteSocketAddress() +
                                           " vs " + client2.getRemoteSocketAddress());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;  // 서버 소켓 예외 시 루프 탈출
            }
        }
        // 루프를 빠져나오면 ServerSocket 닫기
        try {
            serverSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 5000;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        try {
            new TTTServer(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}