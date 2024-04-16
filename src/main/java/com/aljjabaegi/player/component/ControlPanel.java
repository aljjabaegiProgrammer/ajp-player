package com.aljjabaegi.player.component;

import com.aljjabaegi.player.component.util.ComponentUtil;
import com.aljjabaegi.player.config.MessageConfig;
import com.aljjabaegi.player.control.Control;
import com.aljjabaegi.player.control.ControlService;
import com.aljjabaegi.player.util.ApplicationContextHolder;
import com.aljjabaegi.player.util.Utils;
import com.aljjabaegi.player.util.setting.SettingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * control panel (제어 영역)
 *
 * @author GEONLEE
 * @since 2024-04-16
 */
public class ControlPanel extends JPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControlPanel.class);
    private final CustomMediaPlayer mediaPlayer;
    private final JSplitPane splitPane;
    private final JFrame container;
    private final CustomSlider slider;
    private final Color clickColor;
    private final JComboBox<String> presetComboBox = new JComboBox<>();
    private final Map<String, JButton> playButtonMap = new HashMap<>();
    private final Map<Control, JButton> controlButtonMap = new HashMap<>();
    private final Map<Control, Boolean> buttonPressMap = new HashMap<>();
    private final MessageConfig messageConfig;
    private final ControlService controlService;
    private String selectedIp, selectedType;

    public ControlPanel(JFrame container, CustomMediaPlayer mediaPlayer, JSplitPane splitPane) {
        super(new FlowLayout());
        this.container = container;
        this.mediaPlayer = mediaPlayer;
        this.splitPane = splitPane;
        this.slider = new CustomSlider(JSlider.HORIZONTAL, 0, 100, 50);
        this.clickColor = new Color(51, 153, 255);
        for (Control control : Control.values()) {
            int type = control.type();
            if (type < 6 || type > 9) { /*대각선 이동을 제외 하고 추가*/
                this.buttonPressMap.put(control, false);
            }
        }
        this.messageConfig = ApplicationContextHolder.getContext().getBean(MessageConfig.class);
        this.controlService = ApplicationContextHolder.getContext().getBean(ControlService.class);
        initGUI();
    }

    private void initGUI() {
        generateVideoPanel();
        generateSpeedPanel();
        generatePanTiltPanel();
        generateZoomPanel();
        generateWiperPanel();
        generatePresetPanel();
    }

    /**
     * Generate video play/stop control panel
     */
    private void generateVideoPanel() {
        JPanel playPanel = new JPanel(new FlowLayout());
        ComponentUtil.addTitleBorder(playPanel, "Video");
        JButton playButton = generateControlButton(null, "PLAY", "[P]");
        JButton stopButton = generateControlButton(null, "STOP", "[O]");
        addKeyboardListener(KeyEvent.VK_P, null);
        addKeyboardListener(KeyEvent.VK_O, null);
        playPanel.add(playButton);
        playPanel.add(stopButton);
        super.add(playPanel);
    }

    /**
     * Generate PTZ speed control panel
     */
    private void generateSpeedPanel() {
        JPanel speedPanel = new JPanel(new FlowLayout());
        ComponentUtil.addTitleBorder(speedPanel, "PTZ Speed");
        addKeyboardListener(KeyEvent.VK_HOME, null);
        addKeyboardListener(KeyEvent.VK_END, null);
        speedPanel.add(this.slider);
        super.add(speedPanel);
    }

    /**
     * Generate pan, tilt control panel
     */
    private void generatePanTiltPanel() {
        JPanel panTilePanel = new JPanel(new FlowLayout());
        ComponentUtil.addTitleBorder(panTilePanel, "Pan-Tilt");
        JButton leftButton = generateControlButton(Control.LEFT, "LEFT", "[←]");
        JButton rightButton = generateControlButton(Control.RIGHT, "RIGHT", "[→]");
        JButton upButton = generateControlButton(Control.UP, "UP", "[↑]");
        JButton downButton = generateControlButton(Control.DOWN, "DOWN", "[↓]");
        this.controlButtonMap.put(Control.LEFT, leftButton);
        this.controlButtonMap.put(Control.RIGHT, rightButton);
        this.controlButtonMap.put(Control.UP, upButton);
        this.controlButtonMap.put(Control.DOWN, downButton);
        // add keyboard control event
        addKeyboardListener(KeyEvent.VK_LEFT, Control.LEFT);
        addKeyboardListener(KeyEvent.VK_RIGHT, Control.RIGHT);
        addKeyboardListener(KeyEvent.VK_UP, Control.UP);
        addKeyboardListener(KeyEvent.VK_DOWN, Control.DOWN);
        panTilePanel.add(leftButton);
        panTilePanel.add(rightButton);
        panTilePanel.add(upButton);
        panTilePanel.add(downButton);
        super.add(panTilePanel);
    }

    /**
     * Generate zoom control panel
     */
    private void generateZoomPanel() {
        JPanel zoomPanel = new JPanel(new FlowLayout());
        ComponentUtil.addTitleBorder(zoomPanel, "Zoom");
        JButton zoomInButton = generateControlButton(Control.ZOOM_IN, "IN", "[Page Up]");
        JButton zoomOutButton = generateControlButton(Control.ZOOM_OUT, "OUT", "[Page Down]");
        this.controlButtonMap.put(Control.ZOOM_IN, zoomInButton);
        this.controlButtonMap.put(Control.ZOOM_OUT, zoomOutButton);
        // add keyboard control event
        addKeyboardListener(KeyEvent.VK_PAGE_UP, Control.ZOOM_IN);
        addKeyboardListener(KeyEvent.VK_PAGE_DOWN, Control.ZOOM_OUT);
        zoomPanel.add(zoomInButton);
        zoomPanel.add(zoomOutButton);
        super.add(zoomPanel);
    }

    /**
     * Generate wiper control panel
     */
    private void generateWiperPanel() {
        JPanel wiperPanel = new JPanel(new FlowLayout());
        ComponentUtil.addTitleBorder(wiperPanel, "Wiper");
        JButton wiperButton = generateControlButton(Control.WIPER, "WIPER", "[W]");
        this.controlButtonMap.put(Control.WIPER, wiperButton);
        // add keyboard control event
        addKeyboardListener(KeyEvent.VK_W, Control.WIPER);
        wiperPanel.add(wiperButton);
        super.add(wiperPanel);
    }

    /**
     * Generate preset control panel
     */
    private void generatePresetPanel() {
        JPanel presetPanel = new JPanel(new FlowLayout());
        ComponentUtil.addTitleBorder(presetPanel, "Preset");
        JButton presetMoveButton = generateControlButton(Control.PRESET_MOVE, "MOVE", "[M]");
        JButton presetSaveButton = generateControlButton(Control.PRESET_SAVE, "SAVE", "[S]");
        JButton presetDeleteButton = generateControlButton(Control.PRESET_DELETE, "DELETE", "[D]");
        this.controlButtonMap.put(Control.PRESET_MOVE, presetMoveButton);
        this.controlButtonMap.put(Control.PRESET_SAVE, presetSaveButton);
        this.controlButtonMap.put(Control.PRESET_DELETE, presetDeleteButton);
        // add keyboard control event
        addKeyboardListener(KeyEvent.VK_1, null);
        addKeyboardListener(KeyEvent.VK_2, null);
        addKeyboardListener(KeyEvent.VK_3, null);
        addKeyboardListener(KeyEvent.VK_4, null);
        addKeyboardListener(KeyEvent.VK_5, null);
        addKeyboardListener(KeyEvent.VK_6, null);
        addKeyboardListener(KeyEvent.VK_7, null);
        addKeyboardListener(KeyEvent.VK_8, null);
        addKeyboardListener(KeyEvent.VK_9, null);
        addKeyboardListener(KeyEvent.VK_0, null);
        addKeyboardListener(KeyEvent.VK_M, Control.PRESET_MOVE);
        addKeyboardListener(KeyEvent.VK_S, Control.PRESET_SAVE);
        addKeyboardListener(KeyEvent.VK_D, Control.PRESET_DELETE);

        this.presetComboBox.addActionListener(e -> presetButtonDisabled());
        this.presetComboBox.addItem("PRESET");
        this.presetComboBox.setFocusable(false);
        this.presetComboBox.setEnabled(false);
        this.presetComboBox.setPreferredSize(new Dimension(85, this.presetComboBox.getPreferredSize().height));

        presetPanel.add(this.presetComboBox);
        presetPanel.add(presetMoveButton);
        presetPanel.add(presetSaveButton);
        presetPanel.add(presetDeleteButton);
        super.add(presetPanel);
    }

    public ControlService getControlService() {
        return this.controlService;
    }

    public String getSelectedIp() {
        return this.selectedIp;
    }

    public void setSelectedIp(String ip) {
        this.selectedIp = ip;
    }

    public String getSelectedType() {
        return this.selectedType;
    }

    public void setSelectedType(String type) {
        this.selectedType = type;
    }

    /**
     * return control button
     *
     * @param control control enumeration
     * @return JButton
     */
    public JButton getButton(Control control) {
        return this.controlButtonMap.get(control);
    }

    /**
     * return control button
     *
     * @param key play button key
     * @return JButton
     */
    public JButton getButton(String key) {
        key = key.toUpperCase();
        return this.playButtonMap.get(key);
    }

    private JButton generateControlButton(Control control, String text, String shortcut) {
        String buttonText = text;
        if (shortcut != null) {
            shortcut = "<div style='font-size:8px;'>" + shortcut + "</div>";
            buttonText = "<html><div style='text-align: center; font-size:11px;'>" + text + "<br>" + shortcut + "</div></html>";
        }
        JButton button = new JButton(buttonText);
        button.setFocusable(false);
        button.setEnabled(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        button.setPreferredSize(new Dimension(70, 50));
        button.setFocusPainted(true);
        button.setContentAreaFilled(false);
        button.setBorderPainted(true);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (button.isEnabled()) {
                    changeButtonColor(button, clickColor, clickColor);
                    if (control != null) {
                        boolean result;
                        if (control.name().startsWith("PRESET")) {
                            requestPreset(control);
                            if (control != Control.PRESET_MOVE) {
                                changeButtonColor(button, Color.GRAY, Color.LIGHT_GRAY);
                            }
                        } else {
                            result = requestControl(control);
                            if (!result) changeButtonColor(button, Color.GRAY, Color.LIGHT_GRAY);
                        }
                    } else {
                        if (mediaPlayer.isPlaying()) {
                            stop();
                        } else {
                            if (selectedIp != null) {
                                play();
                            }
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.isEnabled()) {
                    if (control != null && !control.name().startsWith("PRESET")) {
                        requestControl(Control.STOP);
                    }
                }
                changeButtonColor(button, Color.GRAY, Color.LIGHT_GRAY);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        if (control == null) {
            this.playButtonMap.put(text, button);
        } else {
            this.controlButtonMap.put(control, button);
        }
        return button;
    }

    /**
     * 버튼 키 이벤트 생성 메서드 ( 개선 필요 )
     */
    private void addKeyboardListener(int keyCode, Control control) {
        this.container.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int pressKeyCode = e.getKeyCode();
                if (pressKeyCode == keyCode) {
                    if (control == null) {
                        /*1~0 숫자 - 프리셋 번호 변경, speed up/down 단축 키 추가*/
                        if ((pressKeyCode > 47 && pressKeyCode < 58)) {
                            int idx = pressKeyCode - 48;
                            if (idx == 0) idx = 10;
                            if (presetComboBox.isEnabled() && presetComboBox.getSelectedIndex() != idx) {
                                presetComboBox.setSelectedIndex(idx);
                            }
                        } else if (pressKeyCode == 36) {
                            slider.addValue(10);
                        } else if (pressKeyCode == 35) {
                            slider.addValue(-10);
                        } else if (pressKeyCode == 79 && mediaPlayer.isPlaying()) {
                            stop();
                        } else if (pressKeyCode == 80 && !mediaPlayer.isPlaying() && selectedIp != null) {
                            play();
                        }
                        return;
                    }
                    JButton button = controlButtonMap.get(control);
                    if (button.isEnabled()) {
                        changeButtonColor(button, clickColor, clickColor);
                        if (!buttonPressMap.get(Control.UP) && !buttonPressMap.get(Control.LEFT) && !buttonPressMap.get(Control.RIGHT) && !buttonPressMap.get(Control.DOWN) && e.getKeyCode() == KeyEvent.VK_UP) {
                            requestControl(Control.UP);
                        } else if (buttonPressMap.get(Control.UP) && !buttonPressMap.get(Control.LEFT) && e.getKeyCode() == KeyEvent.VK_LEFT) {
                            requestControl(Control.LEFT_UP);
                        } else if (buttonPressMap.get(Control.UP) && !buttonPressMap.get(Control.RIGHT) && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                            requestControl(Control.RIGHT_UP);
                        } else if (!buttonPressMap.get(Control.LEFT) && !buttonPressMap.get(Control.UP) && !buttonPressMap.get(Control.DOWN) && !buttonPressMap.get(Control.RIGHT) && e.getKeyCode() == KeyEvent.VK_LEFT) {
                            requestControl(Control.LEFT);
                        } else if (buttonPressMap.get(Control.LEFT) && !buttonPressMap.get(Control.UP) && e.getKeyCode() == KeyEvent.VK_UP) {
                            requestControl(Control.LEFT_UP);
                        } else if (buttonPressMap.get(Control.LEFT) && !buttonPressMap.get(Control.DOWN) && e.getKeyCode() == KeyEvent.VK_DOWN) {
                            requestControl(Control.LEFT_DOWN);
                        } else if (!buttonPressMap.get(Control.RIGHT) && !buttonPressMap.get(Control.UP) && !buttonPressMap.get(Control.DOWN) && !buttonPressMap.get(Control.LEFT) && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                            requestControl(Control.RIGHT);
                        } else if (buttonPressMap.get(Control.RIGHT) && !buttonPressMap.get(Control.UP) && e.getKeyCode() == KeyEvent.VK_UP) {
                            requestControl(Control.RIGHT_UP);
                        } else if (buttonPressMap.get(Control.RIGHT) && !buttonPressMap.get(Control.DOWN) && e.getKeyCode() == KeyEvent.VK_DOWN) {
                            requestControl(Control.RIGHT_DOWN);
                        } else if (!buttonPressMap.get(Control.DOWN) && !buttonPressMap.get(Control.UP) && !buttonPressMap.get(Control.LEFT) && !buttonPressMap.get(Control.RIGHT) && e.getKeyCode() == KeyEvent.VK_DOWN) {
                            requestControl(Control.DOWN);
                        } else if (buttonPressMap.get(Control.DOWN) && !buttonPressMap.get(Control.LEFT) && e.getKeyCode() == KeyEvent.VK_LEFT) {
                            requestControl(Control.LEFT_DOWN);
                        } else if (buttonPressMap.get(Control.DOWN) && !buttonPressMap.get(Control.RIGHT) && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                            requestControl(Control.RIGHT_DOWN);
                        } else if (!buttonPressMap.get(Control.ZOOM_IN) && e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
                            requestControl(Control.ZOOM_IN);
                        } else if (!buttonPressMap.get(Control.ZOOM_OUT) && e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
                            requestControl(Control.ZOOM_OUT);
                        } else if (!buttonPressMap.get(Control.WIPER) && e.getKeyCode() == KeyEvent.VK_W) {
                            requestControl(Control.WIPER);
                        } else if (!buttonPressMap.get(Control.PRESET_MOVE) && e.getKeyCode() == KeyEvent.VK_M) {
                            requestPreset(Control.PRESET_MOVE);
                        } else if (!buttonPressMap.get(Control.PRESET_SAVE) && e.getKeyCode() == KeyEvent.VK_S) {
                            requestPreset(Control.PRESET_SAVE);
                            changeButtonColor(button, Color.GRAY, Color.LIGHT_GRAY);
                            return; /*confirm 창 때문에 press 가 true 가 되지 않도록 설정*/
                        } else if (!buttonPressMap.get(Control.PRESET_DELETE) && e.getKeyCode() == KeyEvent.VK_D) {
                            requestPreset(Control.PRESET_DELETE);
                            changeButtonColor(button, Color.GRAY, Color.LIGHT_GRAY);
                            return; /*confirm 창 때문에 press 가 true 가 되지 않도록 설정*/
                        }
                    }
                    buttonPressMap.put(control, true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (!mediaPlayer.isPlaying()) {
                    return;
                }
                if (e.getKeyCode() == keyCode && control != null) {
                    JButton button = controlButtonMap.get(control);
                    changeButtonColor(button, Color.GRAY, Color.LIGHT_GRAY);
                    if (buttonPressMap.get(Control.UP) && (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT)) {
                        requestControl(Control.UP);
                    } else if (buttonPressMap.get(Control.LEFT) && (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN)) {
                        requestControl(Control.LEFT);
                    } else if (buttonPressMap.get(Control.RIGHT) && (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN)) {
                        requestControl(Control.RIGHT);
                    } else if (buttonPressMap.get(Control.DOWN) && (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT)) {
                        requestControl(Control.DOWN);
                    }
                    buttonPressMap.put(control, false);
                    if (!buttonPressMap.get(Control.UP) && !buttonPressMap.get(Control.DOWN) && !buttonPressMap.get(Control.LEFT) && !buttonPressMap.get(Control.RIGHT) && control != Control.WIPER) {
                        requestControl(Control.STOP);
                    }
                }
            }
        });
    }

    /*
     * 버튼의 보더와 텍스트 색상을 변경 하는 메서드
     * */
    private void changeButtonColor(JButton button, Color borderColor, Color textColor) {
        SwingUtilities.invokeLater(() -> {
            Border border = BorderFactory.createLineBorder(borderColor);
            button.setBorder(border);
            button.setBorder(border);
            button.setForeground(textColor);
        });
    }

    /**
     * CCTV 영상 stop 를 위한 사전 작업 및 영상 stop 메서드
     * - init preset combobox
     * - stop disable, play enable
     * - control button disabled
     */
    private void stop() {
        this.presetComboBox.setSelectedIndex(0);
        this.playButtonMap.get("STOP").setEnabled(false);
        this.playButtonMap.get("PLAY").setEnabled(true);
        this.mediaPlayer.stop();
        controlMode(false);
    }

    /**
     * CCTV play 를 위한 사전 작업 및 영상 play 메서드
     * - preset 조회 및 설정
     * - mediaPlayerComponent 확인
     */
    public void play() {
        if (this.container.getExtendedState() != JFrame.NORMAL) {
            this.container.setExtendedState(JFrame.NORMAL);
        }
        if (SettingUtil.controllableType.contains(this.selectedType)) {
            ArrayList<String> presetList = this.controlService.getPresetList(this.selectedIp, this.selectedType);
            if (presetList.size() > 0) {
                setPresetCombobox(presetList);
            } else {
                setPresetCombobox(null);
                Utils.showAlertDialog(this.container, "경고", messageConfig.getMsg("PRESET.SEARCH.FAIL.MSG"), JOptionPane.ERROR_MESSAGE, null);
            }
            controlMode(true);
        } else {
            controlMode(false);
        }
        if (this.splitPane.getRightComponent() instanceof JLabel) {
            this.splitPane.setRightComponent(this.mediaPlayer);
        }
        String streamingAddr = this.controlService.getStreamingAddr(this.selectedIp, this.selectedType);
        this.mediaPlayer.play(streamingAddr);
        this.playButtonMap.get("PLAY").setEnabled(false);
        this.playButtonMap.get("STOP").setEnabled(true);
    }


    /*
     * control 요청 사전 작업 처리 메서드
     * */
    private boolean requestControl(Control control) {
        boolean result = controlService.control(this.selectedIp, control, slider.getValue(), this.selectedType);
        if (Control.STOP == control && !result) {
            Utils.showAlertDialog(this.container, "경고", messageConfig.getMsg("CONTROL.FAIL.MSG"), JOptionPane.ERROR_MESSAGE, null);
        }
        return result;
    }

    /*
     * preset 요청 사전 작업 처리 메서드
     * */
    private void requestPreset(Control control) {
        String confirmMsg = messageConfig.getMsg("PRESET.CONFIRM.MSG");
        switch (control) {
            case PRESET_SAVE -> {
                String doSomething = "추가";
                if (this.controlButtonMap.get(Control.PRESET_MOVE).isEnabled()) {
                    doSomething = "갱신";
                }
                confirmMsg = confirmMsg.replace("{do}", doSomething);
            }
            case PRESET_DELETE -> {
                confirmMsg = confirmMsg.replace("{do}", "삭제");
            }
        }
        if (!this.mediaPlayer.isPlaying()) {
            Utils.showAlertDialog(this.container, "경고", messageConfig.getMsg("PRESET.VALID.PLAYING.MSG"), JOptionPane.ERROR_MESSAGE, null);
            return;
        }
        int presetNo = presetComboBox.getSelectedIndex();
        if (presetNo == 0) {
            Utils.showAlertDialog(this.container, "경고", messageConfig.getMsg("PRESET.VALID.PRESET.NUMBER.MSG"), JOptionPane.ERROR_MESSAGE, null);
            return;
        }
        confirmMsg = confirmMsg.replace("{ip}", this.selectedIp);
        confirmMsg = confirmMsg.replace("{presetNo}", String.valueOf(presetNo));
        boolean confirm = true;
        if (control != Control.PRESET_MOVE) {
            confirm = Utils.showConfirmDialog(this.container, confirmMsg);
        }
        if (confirm) {
            boolean result = this.controlService.preset(this.selectedIp, control, presetNo, this.selectedType);
            if (!result) {
                Utils.showAlertDialog(this.container, "경고", "프리셋 요청(" + control + ")에 실패 하였습니다.", JOptionPane.ERROR_MESSAGE, null);
                changeButtonColor(this.controlButtonMap.get(control), Color.GRAY, Color.LIGHT_GRAY);
                return;
            }
            if (control == Control.PRESET_SAVE) {
                Utils.showAlertDialog(this.container, "알림", this.selectedIp + "프리셋(" + presetNo + ") 저장에 성공 하였습니다.", JOptionPane.INFORMATION_MESSAGE, null);
            } else if (control == Control.PRESET_DELETE) {
                Utils.showAlertDialog(this.container, "알림", this.selectedIp + "프리셋(" + presetNo + ") 삭제에 성공 하였습니다.", JOptionPane.INFORMATION_MESSAGE, null);
            }
            if (control != Control.PRESET_MOVE) {
                this.controlService.getPresetList(this.selectedIp, this.selectedType);
                setPresetCombobox(this.controlService.getPresetList(this.selectedIp, this.selectedType));
                presetButtonDisabled();
            }
        }
    }

    /**
     * 프리셋 저장 여부에 따른 활성/비활성 처리 메서드
     */
    private void presetButtonDisabled() {
        try {
            int presetNo = presetComboBox.getSelectedIndex();
            if (presetNo <= 0) {
                this.controlButtonMap.get(Control.PRESET_MOVE).setEnabled(false);
                this.controlButtonMap.get(Control.PRESET_SAVE).setEnabled(false);
                this.controlButtonMap.get(Control.PRESET_DELETE).setEnabled(false);
            } else {
                boolean savedPreset = this.presetComboBox.getItemAt(presetNo).endsWith("saved");
                if (savedPreset) {
                    this.controlButtonMap.get(Control.PRESET_MOVE).setEnabled(true);
                    this.controlButtonMap.get(Control.PRESET_SAVE).setEnabled(true);
                    this.controlButtonMap.get(Control.PRESET_DELETE).setEnabled(true);
                } else {
                    this.controlButtonMap.get(Control.PRESET_MOVE).setEnabled(false);
                    this.controlButtonMap.get(Control.PRESET_SAVE).setEnabled(true);
                    this.controlButtonMap.get(Control.PRESET_DELETE).setEnabled(false);
                }
            }
        } catch (NullPointerException e) {
            LOGGER.error(messageConfig.getMsg("NO.BUTTON.MSG"), e);
        }
    }

    /**
     * 컨트롤 영역 활성화/비 활성화 처리 메서드
     */
    public void controlMode(boolean control) {
        Set<Control> controlKeys = this.controlButtonMap.keySet();
        for (Control key : controlKeys) {
            if (!key.name().startsWith("PRESET")) {
                this.controlButtonMap.get(key).setEnabled(control);
            }
        }
        this.playButtonMap.get("STOP").setEnabled(control);
        this.slider.setEnabled(control);
        this.presetComboBox.setEnabled(control);
    }

    /**
     * preset combobox 데이터 생성 메서드
     */
    private void setPresetCombobox(ArrayList<String> presetList) {
        this.presetComboBox.removeAllItems();
        this.presetComboBox.addItem("PRESET");
        if (presetList != null) {
            for (String preset : presetList) {
                this.presetComboBox.addItem(preset);
            }
        }
    }
}
