/* ---------------------- client/UserInterface.java ---------------------- */
package Client;

public class UserInterface {
    public void renderBoard(int[][] board) {
        System.out.println("Current board:");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                char c = board[i][j] == 1 ? 'X' : board[i][j] == 2 ? 'O' : '-';
                System.out.print(" " + c + " ");
            }
            System.out.println();
        }
    }

    public int[][] parseBoard(String msg) {
        // Expect: BOARD r0c0,r0c1,...;...;...
        String data = msg.substring("BOARD ".length());
        String[] rows = data.split(";");
        int[][] board = new int[3][3];
        for (int i = 0; i < 3; i++) {
            String[] cols = rows[i].split(",");
            for (int j = 0; j < 3; j++) board[i][j] = Integer.parseInt(cols[j]);
        }
        return board;
    }
}