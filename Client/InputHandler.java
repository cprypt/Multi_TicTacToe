/* ---------------------- Client/InputHandler.java ---------------------- */
package Client;

import java.util.*;

public class InputHandler {
    private Scanner sc = new Scanner(System.in);

    public int[] move() {
        while (true) {
            System.out.print("Enter your move (row[0-2] col[0-2]): ");
            int r = sc.nextInt();
            int c = sc.nextInt();
            if (validate(r, c)) return new int[]{r, c};
            System.out.println("Invalid move, try again.");
        }
    }

    public boolean validate(int row, int col) {
        return row >= 0 && row < 3 && col >= 0 && col < 3;
    }
}
