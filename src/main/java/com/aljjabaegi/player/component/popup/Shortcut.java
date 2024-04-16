package com.aljjabaegi.player.component.popup;

import com.aljjabaegi.player.component.util.ComponentUtil;

import javax.swing.*;
import java.awt.*;

/**
 * shortcut popup component
 *
 * @author GEONLEE
 * @since 2024-01-22<br />
 * 2024-04-16 GEONLEE - Refactoring
 */
public class Shortcut {
    private final JPanel shortcutPanel;
    private final Container container;

    public Shortcut(Container container) {
        this.shortcutPanel = new JPanel(new GridLayout(0, 2));
        this.shortcutPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Set padding for the grid panel
        this.container = container;
        initGUI();
    }

    /**
     * setting GUI 생성
     */
    private void initGUI() {
        /*단축키 판넬 설정*/
        JLabel titleShortcut = ComponentUtil.generateLabel("단축키", true);
        titleShortcut.setOpaque(true);
        titleShortcut.setBackground(UIManager.getColor("TitlePane.background"));
        this.shortcutPanel.add(titleShortcut);
        JLabel titleFunction = ComponentUtil.generateLabel("기능", true);
        titleFunction.setOpaque(true);
        titleFunction.setBackground(UIManager.getColor("TitlePane.background"));
        this.shortcutPanel.add(titleFunction);
        this.shortcutPanel.add(ComponentUtil.generateLabel("[↑]", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("Up", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("[↓]", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("Down", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("[←]", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("Left", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("[→]", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("Right", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("[PageUp]", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("Zoom In", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("[PageDown]", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("Zoom Out", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("[Home]", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("Speed Up", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("[End]", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("Speed Down", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("[w]", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("Wiper", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("[1~0]", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("Change Preset Number", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("[m]", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("Preset Move", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("[s]", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("Preset Save", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("[d]", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("Preset Delete", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("[p]", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("Play", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("[o]", true));
        this.shortcutPanel.add(ComponentUtil.generateLabel("Stop", true));
    }

    /**
     * shortcut 팝업 표출
     */
    public void show() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this.container, this.shortcutPanel,
                    "Shortcut", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
