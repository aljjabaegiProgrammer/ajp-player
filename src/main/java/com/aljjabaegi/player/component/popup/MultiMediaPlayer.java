package com.aljjabaegi.player.component.popup;

import com.aljjabaegi.player.PlayerApplication;
import com.aljjabaegi.player.component.CustomTitleBar;
import com.aljjabaegi.player.config.MessageConfig;
import com.aljjabaegi.player.control.Control;
import com.aljjabaegi.player.control.ControlService;
import com.aljjabaegi.player.service.cctv.record.CctvDto;
import com.aljjabaegi.player.util.ApplicationContextHolder;
import com.aljjabaegi.player.util.Utils;
import com.aljjabaegi.player.util.setting.SettingKey;
import com.aljjabaegi.player.util.setting.SettingUtil;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 멀티 영상 동시 표출용 frame
 *
 * @author GEONLEE
 * @since 2024-01-30<br />
 * 2024-04-16 GEONLEE - Refactoring
 */
public class MultiMediaPlayer extends JFrame {
    private final PlayerApplication mainFrame;
    private final MessageConfig messageConfig;
    private final ControlService controlService;
    private final Map<Integer, EmbeddedMediaPlayerComponent> mediaComponetMap;
    private final Map<Integer, JComboBox<String>> normalCctvComboBoxMap;
    private final Map<Integer, JComboBox<String>> presetComboBoxMap;

    public MultiMediaPlayer() {
        this.mainFrame = ApplicationContextHolder.getContext().getBean(PlayerApplication.class);
        this.controlService = ApplicationContextHolder.getContext().getBean(ControlService.class);
        this.messageConfig = ApplicationContextHolder.getContext().getBean(MessageConfig.class);
        this.mediaComponetMap = new HashMap<>();
        this.normalCctvComboBoxMap = new HashMap<>();
        this.presetComboBoxMap = new HashMap<>();
        initGUI();
    }

    private void initGUI() {
        setUndecorated(true);
        setIconImage(Objects.requireNonNull(Utils.getImage("image/geonmoticon-96x96.png")).getImage()); /*최소화 아이콘*/
        CustomTitleBar titleBar = new CustomTitleBar(this);
        titleBar.setTitle("Multi Media Player");
        JButton reloadButton = titleBar.createTitleButton("⟳");
        reloadButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                boolean confirm = Utils.showConfirmDialog(getContentPane(), messageConfig.getMsg("RELOAD.ALL.CCTV.VIDEO"));
                if (confirm) {
                    reloadAllVideo();
                }
                reloadButton.setBackground(UIManager.getColor("TitlePane.background"));
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                reloadButton.setBackground(UIManager.getColor("Table.background"));
                reloadButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                reloadButton.setBackground(UIManager.getColor("TitlePane.background"));
                reloadButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        titleBar.addCustomButton(reloadButton);
        add(titleBar, BorderLayout.NORTH);
        int division = Integer.parseInt(SettingUtil.getProperty(SettingKey.DIVISION));
        int rows = (division < 4) ? 1 : 2;
        int cols = (division < 6) ? 2 : 3;
        JPanel mainPanel = new JPanel(new GridLayout(rows, cols));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                for (int i = 0; i < division; i++) {
                    normalCctvComboBoxMap.get(i).setSelectedIndex(0);
                    presetComboBoxMap.get(i).setSelectedItem(0);
                    mediaComponetMap.get(i).mediaPlayer().controls().stop();
                }
            }
        });
        for (int i = 0; i < division; i++) {
            JPanel wrapPlayer = new JPanel(new BorderLayout());
            Border border = BorderFactory.createLineBorder(Color.GRAY, 1);
            wrapPlayer.setBorder(border);
            EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
            this.mediaComponetMap.put(i, mediaPlayerComponent);
            wrapPlayer.add(createCctvCombo(i), BorderLayout.NORTH);
            wrapPlayer.add(mediaPlayerComponent, BorderLayout.CENTER);
            mainPanel.add(wrapPlayer);
        }
        add(mainPanel, BorderLayout.CENTER);
        pack();
        setSize(580 * cols, 370 * rows);
        setLocationRelativeTo(null);
    }

    /**
     * 상태가 정상인 CCTV 정보만 combobox item 으로 생성 하는 메서드
     */
    private JPanel createCctvCombo(int index) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ArrayList<CctvDto> list = this.mainFrame.normalCctvList;
        JComboBox<String> normalCctvCombo = new JComboBox<>();
        JComboBox<String> presetCombo = new JComboBox<>();
        presetCombo.setEnabled(false);
        presetCombo.setFocusable(false);
        presetCombo.setName(String.valueOf(index));
        normalCctvCombo.setName(String.valueOf(index));
        normalCctvCombo.setFocusable(false);
        normalCctvCombo.addActionListener(e -> {
            int selectedIndex = normalCctvCombo.getSelectedIndex();
            int index1 = Integer.parseInt(normalCctvCombo.getName());
            EmbeddedMediaPlayerComponent selectedMediaComponent = mediaComponetMap.get(index1);
            if (selectedIndex != 0) {
                CctvDto dto = list.get(selectedIndex - 1);
                String streamingAddress = controlService.getStreamingAddr(dto.ip(), dto.cctvType());
                selectedMediaComponent.mediaPlayer().media().prepare(streamingAddress);
                selectedMediaComponent.mediaPlayer().controls().play();
                ArrayList<String> presetList = controlService.getPresetList(dto.ip(), dto.cctvType());
                setPresetCombobox(index1, presetList);
            } else {
                selectedMediaComponent.mediaPlayer().controls().stop();
            }
        });
        presetCombo.addActionListener(e -> {
            int selectedIndex = normalCctvCombo.getSelectedIndex();
            if (selectedIndex > 0) {
                CctvDto dto = list.get(selectedIndex - 1);
                int presetNo = presetCombo.getSelectedIndex();
                if (presetNo > 0) {
                    controlService.preset(dto.ip(), Control.PRESET_MOVE, presetNo, dto.cctvType());
                }
            }
        });
        normalCctvCombo.addItem("CCTV 선택");
        for (CctvDto dto : list) {
            normalCctvCombo.addItem(dto.cctvName() + "(" + dto.cctvId() + ")");
        }
        presetCombo.addItem("PRESET");
        this.presetComboBoxMap.put(index, presetCombo);
        this.normalCctvComboBoxMap.put(index, normalCctvCombo);
        panel.add(normalCctvCombo);
        panel.add(presetCombo);
        return panel;
    }

    /**
     * 전체 CCTV 영상 재출력 메서드
     */
    private void reloadAllVideo() {
        int division = Integer.parseInt(SettingUtil.getProperty(SettingKey.DIVISION));
        for (int i = 0; i < division; i++) {
            int selectedIndex = this.normalCctvComboBoxMap.get(i).getSelectedIndex();
            if (selectedIndex != 0) {
                this.normalCctvComboBoxMap.get(i).setSelectedIndex(selectedIndex);
            }
        }
    }

    /**
     * 선택한 CCTV 의 프리셋 정보 세팅 메서드
     */
    private void setPresetCombobox(int index, ArrayList<String> presetList) {
        JComboBox<String> presetCombo = this.presetComboBoxMap.get(index);
        presetCombo.setEnabled(false);
        presetCombo.removeAllItems();
        presetCombo.addItem("PRESET");
        if (presetList != null) {
            presetCombo.setEnabled(true);
            for (String preset : presetList) {
                if (preset.contains("-saved")) {
                    presetCombo.addItem(preset);
                }
            }
        }
    }
}
