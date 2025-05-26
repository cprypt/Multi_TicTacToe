/* ---------------------- client/InputHandler.java ---------------------- */
package client;

import java.util.*;

/**
 * 사용자 입력 처리 및 유효성 검사 클래스
 */
public class InputHandler {
    private Scanner sc = new Scanner(System.in);

    /**
     * 행/열 입력 받고, 범위 검사
     * @return [row, col]
     */
    public int[] move() {
        while (true) {
            System.out.print("위치 선택 (행(0/1/2) 열(0/1/2)): ");
            int r = sc.nextInt();
            int c = sc.nextInt();
            if (validate(r, c)) return new int[]{r, c};
            System.out.println("잘못된 위치");
        }
    }

    /**
     * 0<=row,col<=2 여부 검사
     */
    public boolean validate(int row, int col) {
        return row >= 0 && row < 3 && col >= 0 && col < 3;
    }
}
