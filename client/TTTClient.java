package client;

import server.MessageHandler;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Swing GUI 기반 클라이언트
 */
public class TTTClient extends JFrame {
    private SocketClient socketClient;

    // 상단: 플레이어 번호 표시 (클라이언트 1 / 2)
    private JLabel playerLabel = new JLabel("클라이언트 ...", SwingConstants.CENTER);

    // 3x3 게임판을 표현하는 버튼
    private JButton[][] buttons = new JButton[3][3];

    // 하단: 상태 표시 (연결 상태 / 차례 상태 표시)
    private JLabel statusLabel = new JLabel("연결 중...");

    // 서버로부터 할당받은 플레이어 번호 (1 / 2)
    private int myPlayerNumber = 0;

    // 내 턴인지 여부
    private volatile boolean myTurn = false;

    public TTTClient(String host, int port) throws Exception {
        // 1) 네트워크 소켓 연결
        socketClient = new SocketClient(host, port);

        // 2) 윈도우 기본 설정
        setTitle("멀티플레이어 틱택토");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);              // 레이블 + 판 + 상태 바 영역 확보
        setLayout(new BorderLayout());

        // 3) 상단: "클라이언트 X" 레이블
        playerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        add(playerLabel, BorderLayout.NORTH);

        // 4) 중앙: 3x3 버튼판
        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        Font btnFont = new Font(Font.SANS_SERIF, Font.BOLD, 40);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JButton btn = new JButton("-");
                btn.setFont(btnFont);
                btn.setEnabled(false);  // 초기에는 클릭 금지
                final int row = i, col = j;
                btn.addActionListener(e -> sendMove(row, col));
                buttons[i][j] = btn;
                boardPanel.add(btn);
            }
        }
        add(boardPanel, BorderLayout.CENTER);

        // 5) 하단: 상태 레이블
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        add(statusLabel, BorderLayout.SOUTH);

        setVisible(true);

        // 6) 서버 메시지 수신용 쓰레드 시작
        new Thread(this::receiveLoop).start();
    }

    /**
     * 서버로부터 오는 메시지를 계속 받으면서 처리
     */
    private void receiveLoop() {
        try {
            while (true) {
                String msg = socketClient.receiveMessage();
                if (msg == null) break;

                // ① 서버가 보낸 ID 메시지 처리 → 클라이언트 번호 설정
                if (msg.startsWith("ID ")) {
                    String[] tokens = msg.split(" ");
                    myPlayerNumber = Integer.parseInt(tokens[1].trim());
                    SwingUtilities.invokeLater(() ->
                        playerLabel.setText("클라이언트 " + myPlayerNumber)
                    );
                }
                // ② 서버가 보낸 BOARD 메시지 처리 → 즉시 updateBoard로 화면 갱신
                else if (msg.startsWith(MessageHandler.BOARD)) {
                    int[][] board = parseBoard(msg);
                    // Swing 스레드에서 버튼 텍스트를 바꿔주고, 빈 칸 아닌 버튼은 무조건 disabled 처리
                    SwingUtilities.invokeLater(() -> updateBoard(board));
                }
                // ③ 서버가 보낸 MOVE 메시지 처리 → 내 턴
                else if (msg.equals(MessageHandler.MOVE)) {
                    myTurn = true;
                    // Swing 스레드에서 빈 칸만 enable 처리 & 상태 메시지 바꾸기
                    SwingUtilities.invokeLater(() -> {
                        setBoardEnabled(true);
                        statusLabel.setText("당신 차례입니다");
                    });
                }
                // ④ 서버가 보낸 RESULT 메시지 처리 → 게임 종료
                else if (msg.startsWith(MessageHandler.RESULT)) {
                    String resultText = msg.substring((MessageHandler.RESULT + " ").length());
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                            this,
                            resultText,
                            "게임 종료",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        System.exit(0);
                    });
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 버튼 클릭 시 서버에 MOVE 메시지 전송
     */
    private void sendMove(int row, int col) {
        // 내 턴이 아닐 때는 무시
        if (!myTurn) return;

        // 한 번 눌렀으면 더 이상 못 누르게 설정
        myTurn = false;
        setBoardEnabled(false);
        statusLabel.setText("상대방 차례 대기...");

        socketClient.sendMessage(MessageHandler.MOVE + " " + row + "," + col);
    }

    /**
     * 서버의 BOARD 메시지("BOARD r0c0,r0c1,...;...")를 int[][]로 파싱
     */
    private int[][] parseBoard(String msg) {
        String data = msg.substring((MessageHandler.BOARD + " ").length());
        String[] rows = data.split(";");
        int[][] board = new int[3][3];
        for (int i = 0; i < 3; i++) {
            String[] cols = rows[i].trim().split(",");
            for (int j = 0; j < 3; j++) {
                board[i][j] = Integer.parseInt(cols[j].trim());
            }
        }
        return board;
    }

    /**
     * 파싱된 board 배열을 화면 버튼에 반영
     *  - 0 → "-", 1 → "X", 2 → "O"
     *  - 버튼 텍스트가 "X"나 "O"인 칸은 무조건 disabled
     *  - 만약 myTurn == true 라면, 이후에 빈 칸("-") 버튼만 enable
     */
    private void updateBoard(int[][] board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String text = (board[i][j] == 1 ? "X"
                              : board[i][j] == 2 ? "O"
                              : "-");
                buttons[i][j].setText(text);

                // 이미 X나 O가 있으면 disabled로 고정
                if (!text.equals("-")) {
                    buttons[i][j].setEnabled(false);
                }
            }
        }

        // 만약 이미 내 턴 상태였다면, 빈 칸만 다시 enable 처리
        if (myTurn) {
            setBoardEnabled(true);
        }
    }

    /**
     * 버튼을 활성/비활성화. 단, 버튼의 텍스트가 "-"(빈 칸)인 경우에만 enabled 가능.
     */
    private void setBoardEnabled(boolean enabled) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                // empty 칸("-")인 경우에만 enable/disable
                if (buttons[i][j].getText().equals("-")) {
                    buttons[i][j].setEnabled(enabled);
                } else {
                    buttons[i][j].setEnabled(false);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String host = "localhost";
            int port = 5000;
            if (args.length >= 2) {
                host = args[0];
                port = Integer.parseInt(args[1]);
            }
            try {
                new TTTClient(host, port);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    null,
                    "서버 연결 실패: " + ex.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
            }
        });
    }
}