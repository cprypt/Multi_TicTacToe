import java.io.*;
import java.net.*;

public class TTTServer {
    private static final int PORT = 9999;
    private char[][] board = new char[3][3];
    private Socket player1, player2;
    private PrintWriter out1, out2;
    private BufferedReader in1, in2;
    private boolean player1Turn = true;

    public static void main(String[] args) {
        new TTTServer().start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("서버 시작, 포트 " + PORT);
            // 플레이어 연결
            player1 = serverSocket.accept();
            System.out.println("플레이어1 연결됨: " + player1.getInetAddress());
            out1 = new PrintWriter(player1.getOutputStream(), true);
            in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));

            player2 = serverSocket.accept();
            System.out.println("플레이어2 연결됨: " + player2.getInetAddress());
            out2 = new PrintWriter(player2.getOutputStream(), true);
            in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));

            // 초기화 및 환영 메시지
            initBoard();
            out1.println("WELCOME X");
            out2.println("WELCOME O");
            sendBoard();

            // 게임 루프
            while (true) {
                // 현재 플레이어 소켓/스트림 선택
                BufferedReader currIn = player1Turn ? in1 : in2;
                PrintWriter currOut = player1Turn ? out1 : out2;

                currOut.println("YOUR_TURN");
                String line = currIn.readLine();
                if (line == null) break;

                if (line.startsWith("MOVE")) {
                    String[] tok = line.split(" ");
                    int r = Integer.parseInt(tok[1]), c = Integer.parseInt(tok[2]);
                    if (!isValidMove(r, c)) {
                        currOut.println("INVALID");
                        continue;
                    }
                    board[r][c] = player1Turn ? 'X' : 'O';
                    sendBoard();

                    if (checkWin()) {
                        out1.println(player1Turn ? "RESULT WIN" : "RESULT LOSE");
                        out2.println(player1Turn ? "RESULT LOSE" : "RESULT WIN");
                        break;
                    } else if (checkDraw()) {
                        out1.println("RESULT DRAW");
                        out2.println("RESULT DRAW");
                        break;
                    }
                    player1Turn = !player1Turn;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    private void initBoard() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                board[i][j] = ' ';
    }

    private boolean isValidMove(int r, int c) {
        return r >= 0 && r < 3 && c >= 0 && c < 3 && board[r][c] == ' ';
    }

    private void sendBoard() {
        StringBuilder sb = new StringBuilder();
        for (char[] row : board)
            for (char cell : row)
                sb.append(cell);
        out1.println("BOARD " + sb);
        out2.println("BOARD " + sb);
    }

    private boolean checkWin() {
        char p = player1Turn ? 'X' : 'O';
        // 가로·세로·대각선 승리 판정
        for (int i = 0; i < 3; i++)
            if (board[i][0]==p && board[i][1]==p && board[i][2]==p) return true;
        for (int j = 0; j < 3; j++)
            if (board[0][j]==p && board[1][j]==p && board[2][j]==p) return true;
        if (board[0][0]==p && board[1][1]==p && board[2][2]==p) return true;
        if (board[0][2]==p && board[1][1]==p && board[2][0]==p) return true;
        return false;
    }

    private boolean checkDraw() {
        for (char[] row : board)
            for (char cell : row)
                if (cell == ' ') return false;
        return true;
    }

    private void closeConnections() {
        try {
            if (in1 != null) in1.close();
            if (out1 != null) out1.close();
            if (player1 != null) player1.close();
            if (in2 != null) in2.close();
            if (out2 != null) out2.close();
            if (player2 != null) player2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
