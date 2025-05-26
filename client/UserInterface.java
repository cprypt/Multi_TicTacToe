/* ---------------------- client/UserInterface.java ---------------------- */
package client;

/**
 * 사용자 인터페이스 클래스
 * - BOARD 메시지를 화면에 출력
 * - 문자열 파싱 기능 제공
 */
public class UserInterface {
    /**
     * 2D int 보드를 X/O/- 형태로 렌더링
     */
    public void renderBoard(int[][] board) {
        System.out.println("현재 보드 상태:");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                char c = board[i][j] == 1 ? 'X' : board[i][j] == 2 ? 'O' : '-';
                System.out.print(" " + c + " ");
            }
            System.out.println();
        }
    }

    /**
     * "BOARD r0c0,r0c1,r0c2;..." 형식을  int[][]로 변환
     */
    public int[][] parseBoard(String msg) {
        String data = msg.substring(MessageHandler.BOARD.length() + 1);
        String[] rows = data.split(";");
        int[][] board = new int[3][3];
        for (int i = 0; i < 3; i++) {
            String[] cols = rows[i].trim().split(",");
            for (int j = 0; j < 3; j++) board[i][j] = Integer.parseInt(cols[j].trim());
        }
        return board;
    }
}
