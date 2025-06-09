package server;

import javax.swing.*;
import java.awt.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 서버 관리용 GUI 콘솔
 * 대기 클라이언트 목록
 * 활성 세션 목록
 */
public class ServerConsole extends JFrame {
    private DefaultListModel<String> waitingModel = new DefaultListModel<>();
    private DefaultListModel<String> sessionsModel = new DefaultListModel<>();
    private ConcurrentMap<Integer, String> sessionEntries = new ConcurrentHashMap<>();

    public ServerConsole() {
        super("TicTacToe Server Console");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 900);
        setLayout(new GridLayout(1, 2, 10, 0));

        // ▶ 대기 클라이언트 패널
        JPanel waitingPanel = new JPanel(new BorderLayout());
        JList<String> waitingList = new JList<>(waitingModel);
        waitingPanel.add(new JScrollPane(waitingList), BorderLayout.CENTER);
        JLabel waitLabel = new JLabel("Waiting Clients", SwingConstants.CENTER);
        waitLabel.setFont(waitLabel.getFont().deriveFont(Font.BOLD, 14f));
        waitingPanel.add(waitLabel, BorderLayout.SOUTH);

        // ▶ 활성 세션 패널
        JPanel sessionPanel = new JPanel(new BorderLayout());
        JList<String> sessionList = new JList<>(sessionsModel);
        sessionPanel.add(new JScrollPane(sessionList), BorderLayout.CENTER);
        JLabel sessionLabel = new JLabel("Active Sessions", SwingConstants.CENTER);
        sessionLabel.setFont(sessionLabel.getFont().deriveFont(Font.BOLD, 14f));
        sessionPanel.add(sessionLabel, BorderLayout.SOUTH);

        add(waitingPanel);
        add(sessionPanel);

        setVisible(true);
    }

    /** 대기 클라이언트 추가 */
    public void addWaiting(Socket s) {
        String key = s.getRemoteSocketAddress().toString();
        SwingUtilities.invokeLater(() ->
            waitingModel.addElement(key)
        );
    }

    /** 대기 클라이언트 완전 제거 */
    public void removeWaiting(Socket s) {
        String key = s.getRemoteSocketAddress().toString();
        SwingUtilities.invokeLater(() ->
            waitingModel.removeElement(key)
        );
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

    /** 활성 세션 완전 제거 */
    public void removeSession(int sessionId) {
        String entry = sessionEntries.remove(sessionId);
        if (entry != null) {
            SwingUtilities.invokeLater(() ->
                sessionsModel.removeElement(entry)
            );
        }
    }
}