// GameSession.java
package server;

import java.io.*;
import java.net.Socket;

public class GameSession implements Runnable {
    private final int sessionId;
    private final Socket client1, client2;
    private final BufferedReader reader1, reader2;
    private final PrintWriter writer1, writer2;
    private final GameLogic gameLogic;
    private final MessageHandler messageHandler;
    private final ServerConsole console;

    public GameSession(int sessionId, Socket c1, Socket c2, ServerConsole console) throws IOException {
        this.sessionId = sessionId;
        this.client1 = c1;
        this.client2 = c2;
        this.console = console;

        reader1 = new BufferedReader(new InputStreamReader(c1.getInputStream()));
        writer1 = new PrintWriter(c1.getOutputStream(), true);
        reader2 = new BufferedReader(new InputStreamReader(c2.getInputStream()));
        writer2 = new PrintWriter(c2.getOutputStream(), true);

        this.gameLogic = new GameLogic();
        this.messageHandler = new MessageHandler();
    }

    private void broadcast(String msg) {
        writer1.println(msg);
        writer2.println(msg);
    }

    private void sendTo(int player, String msg) {
        (player==1?writer1:writer2).println(msg);
    }

    private String receiveFrom(int player) throws IOException {
        return (player==1?reader1:reader2).readLine();
    }

    @Override
    public void run() {
        String result = "";
        try {
            sendTo(1, "ID 1");
            sendTo(2, "ID 2");

            int current = 1;
            while (true) {
                broadcast(messageHandler.formatBoard(gameLogic.getBoard()));
                sendTo(current, MessageHandler.MOVE);

                String mv = receiveFrom(current);
                int[] m = messageHandler.parseMove(mv);
                if (!gameLogic.move(m[0], m[1], current)) {
                    result = "유효하지 않은 선택: 클라이언트 " + current;
                    sendTo(current, messageHandler.formatResult(result));
                    break;
                }
                if (gameLogic.checkWinner()==current) {
                    result = "클라이언트 " + current + " 승리";
                    broadcast(messageHandler.formatBoard(gameLogic.getBoard()));
                    broadcast(messageHandler.formatResult(result));
                    break;
                }
                if (gameLogic.isDraw()) {
                    result = "무승부";
                    broadcast(messageHandler.formatBoard(gameLogic.getBoard()));
                    broadcast(messageHandler.formatResult(result));
                    break;
                }
                current = 3 - current;
            }
        } catch (IOException e) {
            result = "세션 에러";
            e.printStackTrace();
        } finally {
            console.endSession(sessionId, result);
            console.removeClient(client1);
            console.removeClient(client2);
            try {
                reader1.close(); writer1.close(); client1.close();
                reader2.close(); writer2.close(); client2.close();
            } catch(IOException ignored){}
        }
    }
}
