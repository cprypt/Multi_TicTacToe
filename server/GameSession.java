package server;

import java.io.*;
import java.net.Socket;

/**
 * GameSession: 두 명의 클라이언트를 묶어,
 *   - 메시지 송수신(BOARD, MOVE, RESULT 등)
 *   - GameLogic을 통한 판 갱신 및 승패 판단
 * 을 담당하는 독립 세션 쓰레드
 */
public class GameSession implements Runnable {
    private Socket client1;
    private Socket client2;
    private BufferedReader reader1, reader2;
    private PrintWriter writer1, writer2;
    private GameLogic gameLogic;
    private MessageHandler messageHandler;

    public GameSession(Socket c1, Socket c2) {
        this.client1 = c1;
        this.client2 = c2;
        this.gameLogic = new GameLogic();
        this.messageHandler = new MessageHandler();
        try {
            // 클라이언트 1 스트림 초기화
            reader1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
            writer1 = new PrintWriter(client1.getOutputStream(), true);
            // 클라이언트 2 스트림 초기화
            reader2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
            writer2 = new PrintWriter(client2.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 클라이언트에게 메시지 브로드캐스트
     */
    private void broadcast(String msg) {
        writer1.println(msg);
        writer2.println(msg);
    }

    /**
     * 특정 플레이어(1 또는 2)에게만 메시지 전송
     */
    private void sendTo(int player, String msg) {
        if (player == 1) {
            writer1.println(msg);
        } else {
            writer2.println(msg);
        }
    }

    /**
     * 특정 플레이어(1 또는 2)로부터 메시지 수신
     */
    private String receiveFrom(int player) throws IOException {
        if (player == 1) {
            return reader1.readLine();
        } else {
            return reader2.readLine();
        }
    }

    /**
     * 두 클라이언트 간의 게임 진행 루프
     */
    @Override
    public void run() {
        try {
            // (선택) 세션 시작 시 클라이언트에 ID 발급
            sendTo(1, "ID 1");
            sendTo(2, "ID 2");

            int currentPlayer = 1;
            while (true) {
                // 1) 현재 보드 상태를 모두에게 전송
                broadcast(messageHandler.formatBoard(gameLogic.getBoard()));

                // 2) 현재 플레이어에게만 “MOVE” 신호
                sendTo(currentPlayer, MessageHandler.MOVE);

                // 3) 그 플레이어로부터 실제 움직임 좌표 수신
                String moveMsg = receiveFrom(currentPlayer);
                int[] move = messageHandler.parseMove(moveMsg);

                // 4) 유효성 검사: 실패 시 해당 플레이어 즉시 패배 처리
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
            // 8) 세션 종료 시, 두 클라이언트 소켓 닫기
            try {
                if (reader1 != null) reader1.close();
                if (writer1 != null) writer1.close();
                if (client1 != null && !client1.isClosed()) client1.close();

                if (reader2 != null) reader2.close();
                if (writer2 != null) writer2.close();
                if (client2 != null && !client2.isClosed()) client2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}