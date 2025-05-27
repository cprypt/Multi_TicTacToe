/* ---------------------- client/TTTClient.java ---------------------- */
package client;

import server.MessageHandler;

/**
 * 클라이언트 메인 클래스
 * - 서버 연결
 * - 메시지 처리(보드, MOVE 요청, 결과 출력)
 */
public class TTTClient {
    private SocketClient sc;
    private UserInterface ui;
    private InputHandler ih;

    public TTTClient(String host, int port) throws Exception {
        sc = new SocketClient(host, port);
        ui = new UserInterface();
        ih = new InputHandler();
        run();
    }

    /**
     * 메시지 수신 및 처리 루프
     */
    private void run() throws Exception {
        while (true) {
            String msg = sc.receiveMessage();
            if (msg == null) break;
            if (msg.startsWith(MessageHandler.BOARD)) {
                int[][] board = ui.parseBoard(msg);
                ui.renderBoard(board);
            } else if (msg.equals(MessageHandler.MOVE)) {
                int[] mv = ih.move();
                sc.sendMessage("MOVE " + mv[0] + "," + mv[1]);
            } else if (msg.startsWith(MessageHandler.RESULT)) {
                System.out.println(msg.substring(MessageHandler.RESULT.length() + 1));
                break;
            }
        }
        sc.disconnect();
    }

    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 5000;
        if (args.length >= 2) { host = args[0]; port = Integer.parseInt(args[1]); }
        new TTTClient(host, port);
    }
}
