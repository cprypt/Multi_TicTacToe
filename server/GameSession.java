package server;

import java.io.*;
import java.net.Socket;

/**
 * GameSession: 두 명의 클라이언트를 묶어 세션 처리
 * 메시지 송수신 (BROADCAST/MOVE/RESULT)
 * GameLogic으로 판 갱신 및 승패 판단
 * 종료 시 ServerConsole에 알려 줌
 */
public class GameSession implements Runnable {
    private final int sessionId;
    private final Socket client1, client2;
    private final BufferedReader reader1, reader2;
    private final PrintWriter writer1, writer2;
    private final GameLogic gameLogic;
    private final MessageHandler messageHandler;
    private final ServerConsole console;

    public GameSession(int sessionId,
                       Socket c1, Socket c2,
                       ServerConsole console) throws IOException {
        this.sessionId = sessionId;
        this.client1 = c1;
        this.client2 = c2;
        this.console = console;

        // 스트림 초기화
        reader1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
        writer1 = new PrintWriter(client1.getOutputStream(), true);
        reader2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
        writer2 = new PrintWriter(client2.getOutputStream(), true);

        this.gameLogic = new GameLogic();
        this.messageHandler = new MessageHandler();
    }

    private void broadcast(String msg) {
        writer1.println(msg);
        writer2.println(msg);
    }

    private void sendTo(int player, String msg) {
        (player == 1 ? writer1 : writer2).println(msg);
    }

    private String receiveFrom(int player) throws IOException {
        return (player == 1 ? reader1 : reader2).readLine();
    }

    @Override
    public void run() {
        try {
            // 세션 시작 시 ID 발급
            sendTo(1, "ID 1");
            sendTo(2, "ID 2");

            int currentPlayer = 1;
            while (true) {
                // 1) 보드 상태 전송
                broadcast(messageHandler.formatBoard(gameLogic.getBoard()));
                // 2) 현재 플레이어에게만 MOVE 신호
                sendTo(currentPlayer, MessageHandler.MOVE);
                // 3) 이동 좌표 수신
                String moveMsg = receiveFrom(currentPlayer);
                int[] move = messageHandler.parseMove(moveMsg);

                // 4) 유효성 검사 실패 시 패배
                if (!gameLogic.move(move[0], move[1], currentPlayer)) {
                    sendTo(currentPlayer, messageHandler.formatResult("잘못된 위치, 패배"));
                    break;
                }
                // 5) 승리 검사
                if (gameLogic.checkWinner() == currentPlayer) {
                    broadcast(messageHandler.formatBoard(gameLogic.getBoard()));
                    broadcast(messageHandler.formatResult("클라이언트 " + currentPlayer + " 승리"));
                    break;
                }
                // 6) 무승부 검사
                if (gameLogic.isDraw()) {
                    broadcast(messageHandler.formatBoard(gameLogic.getBoard()));
                    broadcast(messageHandler.formatResult("무승부"));
                    break;
                }
                // 7) 턴 교체
                currentPlayer = 3 - currentPlayer;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // 세션 종료 알림
            console.removeSession(sessionId);
            // 리소스 정리
            try {
                reader1.close(); writer1.close(); client1.close();
                reader2.close(); writer2.close(); client2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}