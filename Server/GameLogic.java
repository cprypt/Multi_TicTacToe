/* ---------------------- server/GameLogic.java ---------------------- */
package server;

/**
 * 게임 로직 클래스
 * - 3x3 int[][] board 저장(0=빈칸,1=플레이어1,2=플레이어2)
 * - move(), checkWinner(), isDraw() 제공
 */
public class GameLogic {
    private int[][] board;

    public GameLogic() {
        board = new int[3][3];  // 초기 빈 보드
    }

    /**
     * 플레이어의 이동 수행
     * @param row 0~2, @param col 0~2
     * @param player 플레이어 번호(1 또는 2)
     * @return 유효하면 true, 아니면 false
     */
    public boolean move(int row, int col, int player) {
        if (row < 0 || row > 2 || col < 0 || col > 2 || board[row][col] != 0) return false;
        board[row][col] = player;
        return true;
    }

    /**
     * 승자 검사
     * @return 1 또는 2 승리 시 그 플레이어 번호, 없으면 0
     */
    public int checkWinner() {
        // 가로 검사
        for (int i = 0; i < 3; i++)
            if (board[i][0] != 0 && board[i][0] == board[i][1] && board[i][1] == board[i][2])
                return board[i][0];
        // 세로 검사
        for (int j = 0; j < 3; j++)
            if (board[0][j] != 0 && board[0][j] == board[1][j] && board[1][j] == board[2][j])
                return board[0][j];
        // 대각선 검사
        if (board[0][0] != 0 && board[0][0] == board[1][1] && board[1][1] == board[2][2])
            return board[0][0];
        if (board[0][2] != 0 && board[0][2] == board[1][1] && board[1][1] == board[2][0])
            return board[0][2];
        return 0;
    }

    /**
     * 무승부 검사: 빈칸 없고 승자도 없음
     */
    public boolean isDraw() {
        for (int[] row : board) for (int cell : row) if (cell == 0) return false;
        return checkWinner() == 0;
    }

    public int[][] getBoard() {
        return board;
    }
}
