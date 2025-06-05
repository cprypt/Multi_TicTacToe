/* ---------------------- server/MessageHandler.java ---------------------- */
package server;

/**
 * 메시지 포맷 및 파싱 클래스
 * - MOVE, BOARD, RESULT 상수 정의
 * - parseMove, formatBoard, formatResult 제공
 */
public class MessageHandler {
    public static final String MOVE = "MOVE";
    public static final String BOARD = "BOARD";
    public static final String RESULT = "RESULT";

    /**
     * 클라이언트로부터 받은 MOVE 메시지 파싱
     * @return [row, col]
     */
    public int[] parseMove(String msg) {
        if (!msg.startsWith(MOVE)) return new int[]{-1, -1};
        String[] parts = msg.substring(MOVE.length()).trim().split(",");
        return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
    }

    /**
     * 보드 상태를 문자열로 포맷: "BOARD r0c0,r0c1,r0c2;r1c0,...;r2c2"
     */
    public String formatBoard(int[][] board) {
        StringBuilder stringBulider = new StringBuilder(BOARD);
        for (int i = 0; i < 3; i++) {
            stringBulider.append(" ");
            for (int j = 0; j < 3; j++) {
                stringBulider.append(board[i][j]);
                if (j < 2) stringBulider.append(",");
            }
            if (i < 2) stringBulider.append(";");
        }
        return stringBulider.toString();
    }

    /**
     * 게임 결과 메시지 포맷: "RESULT 내용"
     */
    public String formatResult(String result) {
        return RESULT + " " + result;
    }
}