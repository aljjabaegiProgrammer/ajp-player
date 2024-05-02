package com.aljjabaegi.player.component.popup;

import com.aljjabaegi.player.PlayerApplication;
import com.aljjabaegi.player.component.CustomTitleBar;
import com.aljjabaegi.player.config.MessageConfig;
import com.aljjabaegi.player.control.Control;
import com.aljjabaegi.player.control.ControlService;
import com.aljjabaegi.player.server.record.MultiPlayRequest;
import com.aljjabaegi.player.service.cctv.record.CctvDto;
import com.aljjabaegi.player.util.ApplicationContextHolder;
import com.aljjabaegi.player.util.Utils;
import com.aljjabaegi.player.util.setting.SettingKey;
import com.aljjabaegi.player.util.setting.SettingUtil;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.fullscreen.adaptive.AdaptiveFullScreenStrategy;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;

/**
 * 멀티 영상 동시 표출용 frame
 *
 * @author GEONLEE
 * @since 2024-01-30<br />
 * 2024-04-16 GEONLEE - Refactoring<br />
 * 2024-04-30 GEONLEE - http 요청에 의한 영상 출력 시 한 행으로 영상 자동 출력되는 기능 추가<br />
 */
public class MultiMediaPlayer extends JFrame {
    private final PlayerApplication mainFrame;
    private final MessageConfig messageConfig;
    private final ControlService controlService;
    private final Map<Integer, EmbeddedMediaPlayerComponent> mediaComponetMap;
    private final Map<Integer, JComboBox<String>> normalCctvComboBoxMap;
    private final Map<Integer, JComboBox<String>> presetComboBoxMap;

    private final int division;
    private final int rows;
    private final int cols;
    private final int width;
    private final int height;
    private final FullScreenMediaPlayer fullScreenMediaPlayer;
    private MultiPlayRequest multiPlayRequest;

    public MultiMediaPlayer() {
        this.mainFrame = ApplicationContextHolder.getContext().getBean(PlayerApplication.class);
        this.controlService = ApplicationContextHolder.getContext().getBean(ControlService.class);
        this.messageConfig = ApplicationContextHolder.getContext().getBean(MessageConfig.class);
        this.fullScreenMediaPlayer = new FullScreenMediaPlayer();
        this.mediaComponetMap = new HashMap<>();
        this.normalCctvComboBoxMap = new HashMap<>();
        this.presetComboBoxMap = new HashMap<>();
        this.division = Integer.parseInt(SettingUtil.getProperty(SettingKey.DIVISION));
        this.rows = (this.division < 4) ? 1 : 2;
        this.cols = (this.division < 6) ? 2 : 3;
        this.width = 580;
        this.height = 370;
        initGUI();
    }

    public MultiMediaPlayer(MultiPlayRequest multiPlayRequest) {
        this.mainFrame = ApplicationContextHolder.getContext().getBean(PlayerApplication.class);
        this.controlService = ApplicationContextHolder.getContext().getBean(ControlService.class);
        this.messageConfig = ApplicationContextHolder.getContext().getBean(MessageConfig.class);
        this.fullScreenMediaPlayer = new FullScreenMediaPlayer();
        this.mediaComponetMap = new HashMap<>();
        this.normalCctvComboBoxMap = new HashMap<>();
        this.presetComboBoxMap = new HashMap<>();
        this.multiPlayRequest = multiPlayRequest;
        this.division = multiPlayRequest.cctvIds().size();
        this.rows = 1;
        this.cols = division;
        this.width = 270;
        this.height = 210;
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
        JPanel mainPanel = new JPanel(new GridLayout(this.rows, this.cols));
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
        for (int i = 0; i < this.division; i++) {
            JPanel wrapPlayer = new JPanel(new BorderLayout());
            Border border = BorderFactory.createLineBorder(Color.GRAY, 1);
            wrapPlayer.setBorder(border);
            EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
            this.mediaComponetMap.put(i, mediaPlayerComponent);
//            mediaPlayerComponent.videoSurfaceComponent().requestFocusInWindow();
            mediaPlayerComponent.mediaPlayer().fullScreen().strategy(new AdaptiveFullScreenStrategy(this));
            // 더블 클릭 시 전체 화면 토글 이벤트 추가???????????
            mediaPlayerComponent.videoSurfaceComponent().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
//                        mediaPlayerComponent.mediaPlayer().fullScreen().toggle();
                        if (mediaPlayerComponent.mediaPlayer().status().isPlaying()) {
                            String streamingAddr = mediaPlayerComponent.mediaPlayer().media().info().mrl();
                            fullScreenMediaPlayer.play(streamingAddr);
                        }
                    }
                }
            });
            wrapPlayer.add(createCctvCombo(i), BorderLayout.NORTH);
            wrapPlayer.add(mediaPlayerComponent, BorderLayout.CENTER);
            mainPanel.add(wrapPlayer);
        }
//        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(mainPanel, BorderLayout.CENTER);
        pack();
        int width = Math.min(this.width * this.cols, 1900);
        setSize(width + 20, this.height * this.rows);
        setLocationRelativeTo(null);
        if (this.multiPlayRequest != null) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int screenWidth = screenSize.width;
            int screenHeight = screenSize.height;
            setLocation((screenWidth - getWidth()) / 2, screenHeight - getHeight() - 40);
            autoPlay();
        }
    }

    /**
     * 요청에 의한 CCTV 출력 시 자동으로 영상 출력
     *
     * @author GEONLEE
     * @since 2024-04-30
     */
    private void autoPlay() {
        java.util.List<String> cctvIds = this.multiPlayRequest.cctvIds();
        ArrayList<CctvDto> list = this.mainFrame.normalCctvList;
        for (int i = 0, n = cctvIds.size(); i < n; i++) {
            for (int j = 0, m = list.size(); j < m; j++) {
                if (cctvIds.get(i).equals(list.get(j).cctvId())) {
                    this.normalCctvComboBoxMap.get(i).setSelectedIndex(j + 1);
                    ArrayList<String> presetList = controlService.getPresetList(list.get(j).ip(), list.get(j).cctvType());
                    setPresetCombobox(i, presetList);
                    break;
                }
            }
        }
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
            if (selectedIndex > 0) {
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
     * 전체 CCTV 영상 재출력 메서드<br />
     * 환경변수 설정 불러오는 부분을 생성자로 이동
     */
    private void reloadAllVideo() {
//        int division = Integer.parseInt(SettingUtil.getProperty(SettingKey.DIVISION));
        for (int i = 0; i < this.division; i++) {
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

    /**
     * 정상 CCTV 콤보박스 데이터 갱신
     */
    public void reloadNormalCctvCombos() {
        ArrayList<CctvDto> list = this.mainFrame.normalCctvList;
        if (list.size() == 0) {
            return;
        }
        Set<Integer> keys = this.normalCctvComboBoxMap.keySet();
        for (Integer key : keys) {
            JComboBox<String> combo = this.normalCctvComboBoxMap.get(key);
            combo.removeAllItems();
            combo.addItem("CCTV 선택");
            for (CctvDto cctvDto : list) {
                combo.addItem(cctvDto.cctvName() + "(" + cctvDto.cctvId() + ")");
            }
        }
    }
}
