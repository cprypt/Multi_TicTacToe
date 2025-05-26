/* ---------------------- server/MessageHandler.java ---------------------- */
package Server;

public class MessageHandler {
    public static final String MOVE = "MOVE";
    public static final String BOARD = "BOARD";
    public static final String RESULT = "RESULT";

    public int[] parseMove(String msg) {
        // Expect: MOVE row,col
        if (!msg.startsWith(MOVE)) return new int[]{-1, -1};
        String[] parts = msg.substring(MOVE.length()).trim().split(",");
        return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
    }

    public String formatBoard(int[][] board) {
        StringBuilder sb = new StringBuilder(BOARD);
        for (int i = 0; i < 3; i++) {
            sb.append(" ");
            for (int j = 0; j < 3; j++) {
                sb.append(board[i][j]);
                if (j < 2) sb.append(",");
            }
            if (i < 2) sb.append(";");
        }
        return sb.toString();
    }

    public String formatResult(String result) {
        return RESULT + " " + result;
    }
}