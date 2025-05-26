/* ---------------------- server/TTTServer.java ---------------------- */
package server;

/**
 * 서버 메인 클래스
 * - SocketManager로 클라이언트 연결 관리
 * - GameLogic으로 게임 상태 업데이트
 * - MessageHandler로 메시지 포맷 변환
 */
public class TTTServer {
    private SocketManager sm;    // 클라이언트 연결 관리자
    private GameLogic gl;        // 게임 로직 처리기
    private MessageHandler mh;   // 메시지 포맷 및 파싱

    /**
     * 생성자: 포트 열고 클라이언트 연결 대기, 게임 루프 실행
     * @param port 열 포트 번호
     */
    public TTTServer(int port) throws Exception {
        gl = new GameLogic();
        mh = new MessageHandler();
        sm = new SocketManager(port);
        sm.start();           // 두 클라이언트 연결 대기
        runGame();            // 게임 진행
    }

    /**
     * 게임 진행 루프
     * - 번갈아가며 MOVE 요청
     * - move 수행 후 승리/무승부 검사
     * - 게임 종료 시 모든 클라이언트에 결과 전송
     */
    private void runGame() throws Exception {
        int currentPlayer = 1;  // 현재 플레이어 번호(1 또는 2)
        while (true) {
            // 1) 보드 상태를 모든 클라이언트에게 전송
            sm.broadcast(mh.formatBoard(gl.getBoard()));
            // 2) 현재 플레이어에게 움직임 요청
            sm.broadcast(MessageHandler.MOVE);

            // 3) 클라이언트로부터 MOVE 메시지 수신
            String moveMsg = sm.receiveFrom(currentPlayer);
            int[] move = mh.parseMove(moveMsg);

            // 4) 잘못된 이동이면 즉시 패배 처리
            if (!gl.move(move[0], move[1], currentPlayer)) {
                sm.sendTo(currentPlayer, mh.formatResult("Invalid move, you lose."));
                break;
            }
            // 5) 승자 검사
            if (gl.checkWinner() == currentPlayer) {
                sm.broadcast(mh.formatBoard(gl.getBoard()));
                sm.broadcast(mh.formatResult("Player " + currentPlayer + " wins!"));
                break;
            }
            // 6) 무승부 검사
            if (gl.isDraw()) {
                sm.broadcast(mh.formatBoard(gl.getBoard()));
                sm.broadcast(mh.formatResult("Draw"));
                break;
            }
            // 7) 다음 플레이어로 교체
            currentPlayer = 3 - currentPlayer;
        }
        sm.stop();  // 소켓 종료
    }

    /**
     * 애플리케이션 진입점
     * @param args[0] 사용할 포트(기본 5000)
     */
    public static void main(String[] args) throws Exception {
        int port = 5000;
        if (args.length >= 1) port = Integer.parseInt(args[0]);
        System.out.println("Starting TicTacToe server on port " + port);
        new TTTServer(port);
    }
}
