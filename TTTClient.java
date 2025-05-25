import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TTTClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 9999;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new TTTClient().start();
    }

    public void start() {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // 환영 메시지 수신
            String welcome = in.readLine();  // e.g. "WELCOME X"
            char myMark = welcome.charAt(welcome.length() - 1);
            System.out.println("게임에 접속했습니다. 당신의 표시: " + myMark);

            while (true) {
                String line = in.readLine();
                if (line == null) break;

                if (line.startsWith("BOARD")) {
                    printBoard(line.substring(6));
                }
                else if (line.equals("YOUR_TURN")) {
                    System.out.print("이동할 좌표 입력 (row col): ");
                    int r = scanner.nextInt(), c = scanner.nextInt();
                    out.println("MOVE " + r + " " + c);
                }
                else if (line.equals("INVALID")) {
                    System.out.println("잘못된 움직임입니다. 다시 시도하세요.");
                }
                else if (line.startsWith("RESULT")) {
                    String res = line.split(" ")[1];
                    switch (res) {
                        case "WIN":  System.out.println("축하합니다! 승리했습니다."); break;
                        case "LOSE": System.out.println("패배했습니다.");          break;
                        case "DRAW": System.out.println("무승부입니다.");         break;
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private void printBoard(String s) {
        System.out.println("현재 보드:");
        for (int i = 0; i < 3; i++) {
            System.out.println(" " + s.charAt(3*i) + " | " + s.charAt(3*i+1) + " | " + s.charAt(3*i+2));
            if (i < 2) System.out.println("---+---+---");
        }
    }
}
