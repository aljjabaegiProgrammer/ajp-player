package com.aljjabaegi.player.component.popup;

import com.aljjabaegi.player.PlayerApplication;
import com.aljjabaegi.player.component.util.ComponentUtil;
import com.aljjabaegi.player.util.Utils;
import com.aljjabaegi.player.util.connection.DBConnection;
import com.aljjabaegi.player.util.setting.SettingKey;
import com.aljjabaegi.player.util.setting.SettingUtil;
import org.springframework.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * settings popup menu
 *
 * @author GEON LEE
 * @since 2024-01-22<br />
 * 2024-04-16 GEONLEE - Refactoring
 */
public class Settings {
    private final JPanel settingsPanel;
    private final Container container;
    private final HashMap<String, JComponent> textFieldMap;

    public Settings(Container container) {
        this.container = container;
        this.settingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.textFieldMap = new HashMap<>();
        initGUI();
    }

    /**
     * setting GUI 생성
     */
    private void initGUI() {
        JPanel settingMain = new JPanel();
        settingMain.setLayout(new BoxLayout(settingMain, BoxLayout.Y_AXIS));

        JPanel dbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ComponentUtil.addTitleBorder(dbPanel, "Database");

        addNewLabelWithField(dbPanel, SettingKey.URL, new Dimension(300, 30), null);
        addNewLabelWithField(dbPanel, SettingKey.USERNAME, new Dimension(80, 30), null);
        addNewLabelWithField(dbPanel, SettingKey.PASSWORD, new Dimension(80, 30), null);

        JButton testButton = ComponentUtil.generateButton("Test");
        testButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                String url = ((JTextField) textFieldMap.get(SettingKey.URL.value())).getText();
                if (!StringUtils.hasText(url)) {
                    Utils.showAlertDialog(container, "경고", SettingKey.URL.value() + " 을 입력하세요!",
                            JOptionPane.ERROR_MESSAGE, null);
                    return;
                }
                String userName = ((JTextField) textFieldMap.get(SettingKey.USERNAME.value())).getText();
                if (!StringUtils.hasText(userName)) {
                    Utils.showAlertDialog(container, "경고", SettingKey.USERNAME.value() + " 을 입력하세요!",
                            JOptionPane.ERROR_MESSAGE, null);
                    return;
                }
                String password = ((JTextField) textFieldMap.get(SettingKey.PASSWORD.value())).getText();
                if (!StringUtils.hasText(password)) {
                    Utils.showAlertDialog(container, "경고", SettingKey.PASSWORD.value() + " 을 입력하세요!",
                            JOptionPane.ERROR_MESSAGE, null);
                    return;
                }
                try {
                    DBConnection connection = new DBConnection(url, userName, password);
                    if (connection.test()) {
                        Utils.showAlertDialog(container, "알림", "DB 연결에 성공하였습니다.",
                                JOptionPane.INFORMATION_MESSAGE, null);
                    }
                } catch (RuntimeException | SQLException ex) {
                    ex.printStackTrace();
                    Utils.showAlertDialog(container, "경고", "DB 연결에 실패하였습니다.",
                            JOptionPane.ERROR_MESSAGE, null);
                }
            }
        });
        dbPanel.add(testButton);
        settingMain.add(dbPanel);

        JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ComponentUtil.addTitleBorder(connectionPanel, "Connection");

        addNewLabelWithField(connectionPanel, SettingKey.TIMEOUT, new Dimension(100, 30), null);
        settingMain.add(connectionPanel);

        JPanel multiPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ComponentUtil.addTitleBorder(multiPanel, "Multi Media Player");

        JLabel divisionLabel = ComponentUtil.generateLabel("Division :", new Dimension(60, 30));
        JComboBox<String> divisionCombo = ComponentUtil.generateCombobox(List.of("2", "4", "6"), new Dimension(100, 30));
        multiPanel.add(divisionLabel);
        divisionCombo.setSelectedItem(SettingUtil.getProperty(SettingKey.DIVISION));
        this.textFieldMap.put(SettingKey.DIVISION.value(), divisionCombo);
        multiPanel.add(divisionCombo);
        settingMain.add(multiPanel);

        JTabbedPane tabs = new JTabbedPane();
        Set<String> keySet = SettingUtil.cctvTypeMap.keySet();
        for (String key : keySet) {
            tabs.addTab(SettingUtil.cctvTypeMap.get(key), createOnvifPanel(key));
        }
        settingMain.add(tabs);
        this.settingsPanel.add(settingMain);
    }

    private JPanel createOnvifPanel(String cctvType) {
        JPanel onvifPanel = new JPanel();
        onvifPanel.setLayout(new BoxLayout(onvifPanel, BoxLayout.Y_AXIS));

        JPanel authPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ComponentUtil.addTitleBorder(authPanel, "Authentication");

        addNewLabelWithField(authPanel, SettingKey.ID, new Dimension(100, 30), cctvType);
        addNewLabelWithField(authPanel, SettingKey.PW, new Dimension(100, 30), cctvType);
        onvifPanel.add(authPanel);

        JPanel playMain = new JPanel();
        playMain.setLayout(new BoxLayout(playMain, BoxLayout.Y_AXIS));
        ComponentUtil.addTitleBorder(playMain, "Play");

        JPanel playPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addNewLabelWithField(playPanel, SettingKey.PORT, new Dimension(100, 30), cctvType);
        addNewLabelWithField(playPanel, SettingKey.CHANNEL, new Dimension(100, 30), cctvType);
        playMain.add(playPanel);
        onvifPanel.add(playMain);

        JPanel onvifMain = new JPanel();
        ComponentUtil.addTitleBorder(onvifMain, "Onvif");
        onvifMain.setLayout(new BoxLayout(onvifMain, BoxLayout.Y_AXIS));
        JPanel onvifPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel onvifPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel onvifPanel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel onvifPanel4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        onvifMain.add(onvifPanel1);
        onvifMain.add(onvifPanel2);
        onvifMain.add(onvifPanel3);
        onvifMain.add(onvifPanel4);

        addNewLabelWithField(onvifPanel1, SettingKey.CONTEXT, new Dimension(100, 30), cctvType);
        addNewLabelWithField(onvifPanel2, SettingKey.STATUS, new Dimension(450, 30), cctvType);
        addNewLabelWithField(onvifPanel3, SettingKey.CONTROL, new Dimension(450, 30), cctvType);
        addNewLabelWithField(onvifPanel4, SettingKey.PRESET, new Dimension(450, 30), cctvType);

        onvifPanel.add(onvifMain);
        return onvifPanel;
    }

    /**
     * settings 팝업 표출
     */
    public void show() {
        int result = JOptionPane.showConfirmDialog(
                this.container,
                this.settingsPanel,
                "Settings",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (result == JOptionPane.OK_OPTION) {
            Set<String> keyset = this.textFieldMap.keySet();
            for (String key : keyset) {
                if (SettingKey.DIVISION.value().equals(key)) {
                    SettingUtil.setProperty(key, String.valueOf(((JComboBox<String>) this.textFieldMap.get(key)).getSelectedItem()));
                } else {
                    SettingUtil.setProperty(key, ((JTextField) this.textFieldMap.get(key)).getText());
                }
            }
            SettingUtil.updateProperties();
            /*설정 값이 바뀔 때 multiMediaPlayer 가 있으면 닫고 새로 설정 하도록 수정 */
            PlayerApplication app = ((PlayerApplication) this.container);
            if (app.multiMediaPlayer != null) {
                app.multiMediaPlayer.dispose();
                app.multiMediaPlayer = null;
            }
        } else {
            SettingUtil.settingProperties.forEach((key, value) -> {
                if (SettingKey.DIVISION.value().equals(key)) {
                    JComboBox<String> comboBox = (JComboBox<String>) this.textFieldMap.get(String.valueOf(key));
                    if (comboBox != null) {
                        comboBox.setSelectedItem(String.valueOf(value));
                    }
                } else {
                    JTextField textField = ((JTextField) this.textFieldMap.get(String.valueOf(key)));
                    if (textField != null) {
                        textField.setText(String.valueOf(value));
                    }
                }
            });
        }
    }

    /**
     * label, text field 를 생성하여 panel 에 추가
     */
    private void addNewLabelWithField(JPanel panel, SettingKey settingKey, Dimension textFieldSize, String cctvType) {
        String labelText = "";
        if (settingKey.value().contains(".")) {
            labelText = settingKey.value().split("\\.")[1];
        } else {
            labelText = Utils.upperCaseFirst(settingKey.value());
        }
        JLabel label = ComponentUtil.generateLabel(labelText + ": ", new Dimension(60, 30));
        String fieldMapKey = settingKey.value();
        String propertyValue = SettingUtil.getProperty(settingKey);
        if (StringUtils.hasText(cctvType)) {
            fieldMapKey = cctvType + "." + settingKey.value();
            propertyValue = SettingUtil.getProperty(settingKey, cctvType);
        }
        JTextField field = ComponentUtil.generateTextField(propertyValue, textFieldSize);
        this.textFieldMap.put(fieldMapKey, field);
        panel.add(label);
        panel.add(field);
    }
}
