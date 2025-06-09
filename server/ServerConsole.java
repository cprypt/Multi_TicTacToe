// ServerConsole.java
package server;

import javax.swing.*;
import java.awt.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 서버 관리용 GUI 콘솔
 * - 현재 접속된 클라이언트 목록
 * - 활성 세션 목록 및 종료 결과 표시
 */
public class ServerConsole extends JFrame {
    private DefaultListModel<String> clientsModel = new DefaultListModel<>();
    private DefaultListModel<String> sessionsModel = new DefaultListModel<>();
    private ConcurrentMap<Integer,String> sessionEntries = new ConcurrentHashMap<>();

    public ServerConsole() {
        super("멀티플레이어 틱택토 서버 관리 콘솔");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new GridLayout(1, 2));

        // 좌측: 접속 클라이언트
        JPanel clientsPanel = new JPanel(new BorderLayout());
        JList<String> clientsList = new JList<>(clientsModel);
        clientsPanel.add(new JScrollPane(clientsList), BorderLayout.CENTER);
        JLabel clientsLabel = new JLabel("연결 클라이언트", SwingConstants.CENTER);
        clientsLabel.setFont(clientsLabel.getFont().deriveFont(Font.BOLD));
        clientsPanel.add(clientsLabel, BorderLayout.SOUTH);
        add(clientsPanel);

        // 우측: 활성 세션
        JPanel sessionsPanel = new JPanel(new BorderLayout());
        JList<String> sessionsList = new JList<>(sessionsModel);
        sessionsPanel.add(new JScrollPane(sessionsList), BorderLayout.CENTER);
        JLabel sessionsLabel = new JLabel("활성 세션", SwingConstants.CENTER);
        sessionsLabel.setFont(sessionsLabel.getFont().deriveFont(Font.BOLD));
        sessionsPanel.add(sessionsLabel, BorderLayout.SOUTH);
        add(sessionsPanel);

        setVisible(true);
    }

    /**
     * 클라이언트 접속 시 목록에 추가
     */
    public void addClient(Socket s) {
        String key = s.getRemoteSocketAddress().toString();
        SwingUtilities.invokeLater(() -> {
            if (!clientsModel.contains(key))
                clientsModel.addElement(key);
        });
    }

    /**
     * 클라이언트 연결 종료 시 목록에서 제거
     */
    public void removeClient(Socket s) {
        String key = s.getRemoteSocketAddress().toString();
        SwingUtilities.invokeLater(() -> clientsModel.removeElement(key));
    }

    /**
     * 세션 시작 시 목록에 추가
     */
    public void addSession(int sessionId, Socket c1, Socket c2) {
        String entry = "#" + sessionId + ": "
            + c1.getRemoteSocketAddress() + " vs "
            + c2.getRemoteSocketAddress();
        sessionEntries.put(sessionId, entry);
        SwingUtilities.invokeLater(() -> sessionsModel.addElement(entry));
    }

    /**
     * 세션 종료 시 결과를 해당 항목 옆에 표시
     */
    public void endSession(int sessionId, String result) {
        String entry = sessionEntries.get(sessionId);
        if (entry == null) return;
        String updated = entry + " - " + result;
        sessionEntries.put(sessionId, updated);
        SwingUtilities.invokeLater(() -> {
            sessionsModel.removeElement(entry);
            sessionsModel.addElement(updated);
        });
    }
}