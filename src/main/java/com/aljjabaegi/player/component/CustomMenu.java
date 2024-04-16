package com.aljjabaegi.player.component;

import com.aljjabaegi.player.PlayerApplication;
import com.aljjabaegi.player.component.util.ComponentUtil;
import com.aljjabaegi.player.config.MessageConfig;
import com.aljjabaegi.player.util.ApplicationContextHolder;
import com.aljjabaegi.player.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Custom 한 메뉴 바
 *
 * @author GEON LEE
 * @apiNote 2024-02-05 GEON LEE - 생성 관련 메서드 를 ComponentUtil 로 이동
 * @since 2024-01-26
 */
public class CustomMenu extends JMenuBar {
    private final Container container;
    private final MessageConfig messageConfig;
    private final PlayerApplication mainFrame;

    public CustomMenu(Container container) {
        this.container = container;
        this.messageConfig = ApplicationContextHolder.getContext().getBean(MessageConfig.class);
        this.mainFrame = ApplicationContextHolder.getContext().getBean(PlayerApplication.class);
        initGUI();
    }

    private void initGUI() {
        JMenu fileMenu = ComponentUtil.generateMenu("File", KeyEvent.VK_F);
        JMenuItem tableReloadMenu = ComponentUtil.generateMenuItem("Reload CCTV Information", KeyEvent.VK_R, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Utils.showConfirmDialog(container, messageConfig.getMsg("RELOAD.CCTV.CONFIRM.MSG"))) {
                    mainFrame.reloadTable();
                }
            }
        });
        JMenuItem closeMenu = ComponentUtil.generateMenuItem("Exit", KeyEvent.VK_X, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Utils.showConfirmDialog(container, messageConfig.getMsg("PROGRAM.CLOSE.MSG"))) {
                    System.exit(0);
                }
            }
        });
        fileMenu.add(tableReloadMenu);
        fileMenu.add(closeMenu);
        JMenu mediaMenu = ComponentUtil.generateMenu("Media", KeyEvent.VK_M);
        JMenuItem rtspStreamMenu = ComponentUtil.generateMenuItem("Open Rtsp Stream", KeyEvent.VK_O, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showOpenRtspDialog();
            }
        });
        JMenuItem multiStreamMenu = ComponentUtil.generateMenuItem("Open Multi Player", KeyEvent.VK_T, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showMultiMediaPlayer();
            }
        });
        JMenuItem settingMenu = ComponentUtil.generateMenuItem("Settings", KeyEvent.VK_S, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.settings.show();
            }
        });
        mediaMenu.add(rtspStreamMenu);
        mediaMenu.add(multiStreamMenu);
        mediaMenu.add(settingMenu);
        JMenu helpMenu = ComponentUtil.generateMenu("Help", KeyEvent.VK_H);

        JMenuItem shortCutMenu = ComponentUtil.generateMenuItem("Shortcut", KeyEvent.VK_C, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.shortcut.show();
            }
        });
        JMenuItem aboutMenu = ComponentUtil.generateMenuItem("About", KeyEvent.VK_A, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.about.show(mainFrame.projectName, mainFrame.projectVersion, mainFrame.javaVersion, mainFrame.springbootVersion);
            }
        });
        helpMenu.add(shortCutMenu);
        helpMenu.add(aboutMenu);
        super.add(fileMenu);
        super.add(mediaMenu);
        super.add(helpMenu);
    }
}
