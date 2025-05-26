/* ---------------------- server/GameLogic.java ---------------------- */
package Server;

public class GameLogic {
    private int[][] board;

    public GameLogic() {
        board = new int[3][3];
    }

    public boolean move(int row, int col, int player) {
        if (row < 0 || row > 2 || col < 0 || col > 2 || board[row][col] != 0) return false;
        board[row][col] = player;
        return true;
    }

    public int checkWinner() {
        // rows
        for (int i = 0; i < 3; i++) if (board[i][0] != 0 && board[i][0] == board[i][1] && board[i][1] == board[i][2]) return board[i][0];
        // cols
        for (int j = 0; j < 3; j++) if (board[0][j] != 0 && board[0][j] == board[1][j] && board[1][j] == board[2][j]) return board[0][j];
        // diags
        if (board[0][0] != 0 && board[0][0] == board[1][1] && board[1][1] == board[2][2]) return board[0][0];
        if (board[0][2] != 0 && board[0][2] == board[1][1] && board[1][1] == board[2][0]) return board[0][2];
        return 0;
    }

    public boolean isDraw() {
        for (int[] row : board) for (int cell : row) if (cell == 0) return false;
        return checkWinner() == 0;
    }

    public int[][] getBoard() {
        return board;
    }
}