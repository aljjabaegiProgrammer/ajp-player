package com.aljjabaegi.player.component;

import com.aljjabaegi.player.PlayerApplication;
import com.aljjabaegi.player.config.MessageConfig;
import com.aljjabaegi.player.util.ApplicationContextHolder;
import com.aljjabaegi.player.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Custom title bar, default title 을 없애고 default title 의 기능을 구현
 * Custom title bar 를 추가 하기 전에 mainFrame 의 default title bar 를 숨겨야 함. setUndecorated(true);
 *
 * @author GEON LEE
 * @since 2024-01-26<br />
 * 2024-04-16 Refactoring
 */
public class CustomTitleBar extends JPanel {
    private final JFrame container;
    private final MessageConfig messageConfig;
    private final JPanel titlePanel;
    private final JLabel titleLabel;
    private final JPanel customButtonArea;
    private int xMouse, yMouse;
    private int locationX, locationY;

    public CustomTitleBar(JFrame container) {
        this.container = container;
        this.messageConfig = ApplicationContextHolder.getContext().getBean(MessageConfig.class);
        this.titlePanel = new JPanel(new BorderLayout());
        this.customButtonArea = new JPanel(new BorderLayout());
        this.titleLabel = new JLabel();
        super.setLayout(new BorderLayout());
        initGUI();
    }

    private void initGUI() {
        this.titlePanel.setBackground(UIManager.getColor("TitlePane.background"));
        this.titlePanel.setPreferredSize(new Dimension(this.container.getWidth(), 30));
        this.titlePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                xMouse = e.getX();
                yMouse = e.getY();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (container.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                        container.setExtendedState(JFrame.NORMAL);
                    } else {
                        container.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    }
                }
            }
        });
        this.titlePanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                locationX = e.getXOnScreen() - xMouse - 30;
                locationY = e.getYOnScreen() - yMouse;
                container.setLocation(locationX, locationY);
            }
        });
        JLabel iconLabel = new JLabel(Utils.getImage("image/geonmoticon-32x32.png"));
        iconLabel.setPreferredSize(new Dimension(30, 10));
        iconLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Table.background")));
        super.setBackground(UIManager.getColor("TitlePane.background"));
        super.add(iconLabel, BorderLayout.WEST);

        this.titleLabel.setForeground(UIManager.getColor("TitlePane.foreground")); // FlatDarculaLaf의 타이틀 바 글자 색상
        this.titleLabel.setHorizontalAlignment(JLabel.CENTER);
        this.titleLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Table.background")));
        this.titlePanel.add(this.titleLabel, BorderLayout.CENTER);
        this.titleLabel.setForeground(Color.GRAY);
        JPanel buttonPanel = new JPanel(new BorderLayout());
        JButton closeButton = createTitleButton("X");
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (container instanceof PlayerApplication) {
                    if (Utils.showConfirmDialog(container, messageConfig.getMsg("PROGRAM.CLOSE.MSG"))) {
                        System.exit(0);
                    }
                } else {
                    if (Utils.showConfirmDialog(container, messageConfig.getMsg("POPUP.CLOSE.MSG"))) {
                        closeButton.setBackground(UIManager.getColor("TitlePane.background"));
                        container.dispose();
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setBackground(Color.RED);
                closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setBackground(UIManager.getColor("TitlePane.background"));
                closeButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        JButton minimizationButton = createTitleButton("ㅡ");
        minimizationButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                container.setExtendedState(JFrame.HIDE_ON_CLOSE);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                minimizationButton.setBackground(UIManager.getColor("Table.background"));
                minimizationButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                minimizationButton.setBackground(UIManager.getColor("TitlePane.background"));
                minimizationButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        JButton fullscreenButton = createTitleButton("□");
        fullscreenButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (container.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                    container.setExtendedState(JFrame.NORMAL);
                } else {
                    container.setExtendedState(JFrame.MAXIMIZED_BOTH);
                }
                container.requestFocus();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                fullscreenButton.setBackground(UIManager.getColor("Table.background"));
                fullscreenButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                fullscreenButton.setBackground(UIManager.getColor("TitlePane.background"));
                fullscreenButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        this.customButtonArea.add(minimizationButton, BorderLayout.CENTER);
        buttonPanel.add(customButtonArea, BorderLayout.WEST);
        buttonPanel.add(fullscreenButton, BorderLayout.CENTER);
        buttonPanel.add(closeButton, BorderLayout.EAST);
        this.titlePanel.add(buttonPanel, BorderLayout.EAST);
        super.add(titlePanel, BorderLayout.CENTER);
    }

    /**
     * 최소화 버튼 왼쪽 버튼을 1개 추가할 수 있는 메서드
     */
    public void addCustomButton(JButton button) {
        this.customButtonArea.add(button, BorderLayout.WEST);
    }

    /**
     * title bar add menu 메서드
     */
    public void addMenu(JMenuBar menu) {
        this.titlePanel.add(menu, BorderLayout.WEST);
    }

    /**
     * header title 변경
     */
    public void setTitle(String text) {
        this.titleLabel.setText(text);
    }

    /**
     * header title button 생성 이벤트
     */
    public JButton createTitleButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(30, this.titlePanel.getHeight()));
        button.setBackground(UIManager.getColor("TitlePane.background"));
        button.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Table.background")));
        return button;
    }
}