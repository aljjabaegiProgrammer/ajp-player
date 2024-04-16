package com.aljjabaegi.player.component.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Component 생성 관련 메서드
 *
 * @author GEONLEE
 * @since 2024-02-05
 */
public class ComponentUtil {

    /**
     * title border 추가 메서드
     */
    public static void addTitleBorder(JComponent component, String borderTitle) {
        component.setBorder(BorderFactory.createTitledBorder(borderTitle));
    }

    /**
     * label 생성 메서드
     */
    public static JLabel generateLabel(String labelText, Dimension size) {
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(size);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    /**
     * label 생성 메서드 with border
     */
    public static JLabel generateLabel(String labelText, boolean border) {
        JLabel label = new JLabel(labelText);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        if (border) {
            label.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Table.background")));
        }
        return label;
    }

    /**
     * TextField 생성 메서드
     */
    public static JTextField generateTextField(String defaultText, Dimension size) {
        JTextField textField = new JTextField(defaultText);
        textField.setPreferredSize(size);
        return textField;
    }

    /**
     * Combobox 생성 메서드
     */
    public static JComboBox<String> generateCombobox(List<String> list, Dimension size) {
        JComboBox<String> combobox = new JComboBox<>();
        combobox.setPreferredSize(size);
        for (String data : list) {
            combobox.addItem(data);
        }
        return combobox;
    }

    /**
     * Tab 생성 메서드
     */
    public static JTabbedPane generateTab(Map<String, JPanel> tabMap) {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                tabs.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                tabs.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        Set<String> set = tabMap.keySet();
        for (String s : set) {
            tabs.addTab(s, tabMap.get(s));
        }
        return tabs;
    }

    /**
     * 상위 메뉴 생성 메서드
     */
    public static JMenu generateMenu(String menuName, int key) {
        JMenu menu = new JMenu(menuName);
        menu.setMnemonic(key);
        return menu;
    }

    /**
     * 메뉴 아이템(하위 메뉴) 생성 메서드
     */
    public static JMenuItem generateMenuItem(String menuItemName, int key, ActionListener event) {
        JMenuItem menuItem = new JMenuItem(menuItemName);
        KeyStroke menuItemStroke = KeyStroke.getKeyStroke(key, InputEvent.ALT_DOWN_MASK);
        menuItem.setAccelerator(menuItemStroke);
        menuItem.addActionListener(event);
        return menuItem;
    }

    public static JButton generateButton(String title) {
        JButton button = new JButton(title);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        button.setPreferredSize(new Dimension(70, 28));
        button.setFocusPainted(true);
        button.setContentAreaFilled(false);
        button.setBorderPainted(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;

    }
}
