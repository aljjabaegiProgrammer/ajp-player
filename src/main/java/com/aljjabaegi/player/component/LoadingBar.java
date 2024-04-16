package com.aljjabaegi.player.component;

import javax.swing.*;
import java.awt.*;

/**
 * JFrame Progress bar
 *
 * @author GEON LEE
 * @apiNote 2024-01-18 GEON LEE showLoadingBar, setMaxLoading, setLoadingText 메서드 추가
 * 2024-01-26 GEON LEE - 로딩바 길이 변경
 * 2024-01-29 GEON LEE - 로딩바 위치 program 위치 변경에 따라 이동 되도록 수정
 * 2024-01-30 GEON LEE - 전역 으로 관리 하던 MAIN FRAME 위치를 container 에서 가져 오도록 수정
 * @since 2024-01-12
 */
public class LoadingBar extends JProgressBar {
    private final JDialog dialog;
    private final JProgressBar progressBar;
    private final JLabel loadingText;

    private final Container container;

    public LoadingBar(Container container) {
        this.container = container;
        JPanel panel = new JPanel(new BorderLayout());
        this.dialog = new JDialog((JFrame) this.container, "Loading", true);
        this.dialog.setUndecorated(true);
        JLayeredPane layeredPane = new JLayeredPane();
        this.loadingText = new JLabel("Loading...");
        Font customFont = new Font("Arial", Font.BOLD, 15);
        this.loadingText.setFont(customFont);
        this.loadingText.setBounds(350, 2, 100, 20);
        this.progressBar = new JProgressBar();
        this.progressBar.setBorderPainted(true);
        this.progressBar.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        this.progressBar.setForeground(new Color(51, 153, 255));
        this.progressBar.setBounds(0, 0, 800, 23);
        layeredPane.add(progressBar, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(loadingText, JLayeredPane.PALETTE_LAYER);
        panel.add(layeredPane, BorderLayout.CENTER);
        this.dialog.add(panel);
        this.dialog.setSize(800, 23);
        this.dialog.setLocationRelativeTo(container);
    }

    /**
     * 로딩바 show/hidden 메서드
     */
    public void showLoadingBar(boolean show) {
        // JFrame의 크기 구하기
        if (show) {
            if (this.container.getX() != 0 && container.getY() != 0) {
                this.dialog.setLocation(container.getX() + 270, container.getY() + 315);
            }
            this.dialog.setVisible(true);
        } else {
            this.loadingText.setText("Loading...");
            this.progressBar.setValue(0);
            this.dialog.dispose();
        }
    }

    /**
     * 로딩바 텍스트 변경 메서드
     */
    public void setLoadingText(int percent) {
        this.loadingText.setText((percent * 100 / progressBar.getMaximum()) + "%");
        this.progressBar.setValue(percent);
    }

    /**
     * 로딩바 max 값 설정
     */
    public void setMaxLoading(int size) {
        this.progressBar.setMaximum(size);
    }
}
