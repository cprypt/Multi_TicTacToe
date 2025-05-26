/* ---------------------- server/TTTServer.java ---------------------- */
package Server;

public class TTTServer {
    private SocketManager sm;
    private GameLogic gl;
    private MessageHandler mh;

    public TTTServer(int port) throws Exception {
        gl = new GameLogic();
        mh = new MessageHandler();
        sm = new SocketManager(port);
        sm.start();
        runGame();
    }

    private void runGame() throws Exception {
        int currentPlayer = 1;
        while (true) {
            sm.broadcast(mh.formatBoard(gl.getBoard()));
            sm.broadcast("MOVE");
            String moveMsg = sm.receiveFrom(currentPlayer);
            int[] move = mh.parseMove(moveMsg);
            if (!gl.move(move[0], move[1], currentPlayer)) {
                sm.sendTo(currentPlayer, "RESULT Invalid move, you lose.");
                break;
            }
            if (gl.checkWinner() == currentPlayer) {
                sm.broadcast(mh.formatBoard(gl.getBoard()));
                sm.broadcast(mh.formatResult("Player " + currentPlayer + " wins!"));
                break;
            }
            if (gl.isDraw()) {
                sm.broadcast(mh.formatBoard(gl.getBoard()));
                sm.broadcast(mh.formatResult("Draw"));
                break;
            }
            currentPlayer = 3 - currentPlayer;
        }
        sm.stop();
    }

    public static void main(String[] args) throws Exception {
        int port = 5000;
        if (args.length >= 1) port = Integer.parseInt(args[0]);
        System.out.println("Starting TicTacToe server on port " + port);
        new TTTServer(port);
    }
}