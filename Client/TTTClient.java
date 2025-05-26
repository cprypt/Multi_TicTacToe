/* ---------------------- client/TTTClient.java ---------------------- */
package Client;

public class TTTClient {
    private SocketClient sc;
    private UserInterface ui;
    private InputHandler ih;

    public TTTClient(String host, int port) throws Exception {
        sc = new SocketClient(host, port);
        ui = new UserInterface();
        ih = new InputHandler();
        run();
    }

    private void run() throws Exception {
        while (true) {
            String msg = sc.receiveMessage();
            if (msg == null) break;
            if (msg.startsWith("BOARD")) {
                int[][] board = ui.parseBoard(msg);
                ui.renderBoard(board);
            } else if (msg.equals("MOVE")) {
                int[] mv = ih.move();
                sc.sendMessage("MOVE " + mv[0] + "," + mv[1]);
            } else if (msg.startsWith("RESULT")) {
                System.out.println(msg.substring("RESULT ".length()));
                break;
            }
        }
        sc.disconnect();
    }

    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 5000;
        if (args.length >= 2) {
            host = args[0]; port = Integer.parseInt(args[1]);
        }
        new TTTClient(host, port);
    }
}