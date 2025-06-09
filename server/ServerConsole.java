package server;

import javax.swing.*;
import java.awt.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 서버 관리 GUI 콘솔
 * 대기 클라이언트 목록
 * 활성 세션 목록
 */
public class ServerConsole extends JFrame {
    private DefaultListModel<String> waitingModel = new DefaultListModel<>();
    private DefaultListModel<String> sessionsModel = new DefaultListModel<>();
    private ConcurrentMap<Integer, String> sessionEntries = new ConcurrentHashMap<>();

    public ServerConsole() {
        super("멀티플레이어 틱택토 서버 관리 콘솔");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new GridLayout(1, 2));

        // 왼쪽: 대기 클라이언트
        JList<String> waitingList = new JList<>(waitingModel);
        add(new JScrollPane(waitingList), BorderLayout.WEST);
        JLabel waitLabel = new JLabel("대기 클라이언트", SwingConstants.CENTER);
        waitLabel.setFont(waitLabel.getFont().deriveFont(Font.BOLD));
        add(waitLabel, BorderLayout.NORTH);

        // 오른쪽: 활성 세션
        JList<String> sessionList = new JList<>(sessionsModel);
        add(new JScrollPane(sessionList), BorderLayout.EAST);
        JLabel sessionLabel = new JLabel("활성 세션", SwingConstants.CENTER);
        sessionLabel.setFont(sessionLabel.getFont().deriveFont(Font.BOLD));
        add(sessionLabel, BorderLayout.SOUTH);

        setVisible(true);
    }

    /** 대기 클라이언트 추가 */
    public void addWaiting(Socket s) {
        SwingUtilities.invokeLater(() ->
            waitingModel.addElement(s.getRemoteSocketAddress().toString())
        );
    }

    /** 대기 클라이언트 제거 */
    public void removeWaiting(Socket s) {
        SwingUtilities.invokeLater(() -> {
            waitingModel.removeElement(s.getRemoteSocketAddress().toString());
        });
    }

    /** 세션 추가 */
    public void addSession(int sessionId, Socket c1, Socket c2) {
        String entry = "#" + sessionId + ": " +
            c1.getRemoteSocketAddress() + " ↔ " +
            c2.getRemoteSocketAddress();
        sessionEntries.put(sessionId, entry);
        SwingUtilities.invokeLater(() ->
            sessionsModel.addElement(entry)
        );
    }

    /** 세션 종료 표시 */
    public void endSession(int sessionId) {
        String entry = sessionEntries.get(sessionId);
        if (entry == null) return;
        String ended = entry + " (종료)";
        sessionEntries.put(sessionId, ended);
        SwingUtilities.invokeLater(() -> {
            sessionsModel.removeElement(entry);
            sessionsModel.addElement(ended);
        });
    }
}