package com.aljjabaegi.player.control;

import com.aljjabaegi.player.util.connection.URLConnection;
import com.aljjabaegi.player.util.setting.SettingUtil;
import com.aljjabaegi.player.util.setting.SettingKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Base64;

/**
 * CCTV 제어 기능 요청
 *
 * @author GEON LEE
 * @apiNote 2024-01-22 GEON LEE - WIPER 기능 추가
 * 2024-02-02 GEON LEE - settingUtil 적용, getPresetList ArrayList<String> type return 하도록 변경
 * @since 2024-01-19
 */
@Service
public class ControlService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControlService.class);

    /**
     * basic auth return 메서드
     */
    private String getBasicAuth(String type) {
        return "Basic " + new String(Base64.getEncoder().encode((SettingUtil.getProperty(SettingKey.ID, type) + ":"
                + SettingUtil.getProperty(SettingKey.PW, type)).getBytes()));
    }

    /**
     * CCTV IP 주소로 RTSP Streaming 주소를 리턴 하는 메서드
     */
    public String getStreamingAddr(String ip, String type) {
        //"rtsp://" + this.ID + ":" + this.PW + "@" + ip + ":" + this.PORT + "/0";
        String id = SettingUtil.getProperty(SettingKey.ID, type);
        String pw = SettingUtil.getProperty(SettingKey.PW, type);
        String authentication = "";
        if (StringUtils.hasText(id) && StringUtils.hasText(pw)) {
            authentication = id + ":" + pw + "@";
        }
        String streamingAddr = "rtsp://" + authentication + ip + ":" + SettingUtil.getProperty(SettingKey.PORT, type) + "/" +
                SettingUtil.getProperty(SettingKey.CHANNEL, type);
        LOGGER.info("[Request] RTSP Streaming : {}", streamingAddr);
        return streamingAddr;
    }

    /**
     * 카메라 제어 메서드 (팬, 틸트, 줌)
     */
    public boolean getStatus(String ip, String type) {
        URLConnection conn = new URLConnection();
        String context = SettingUtil.getProperty(SettingKey.CONTEXT, type);
        String status = SettingUtil.getProperty(SettingKey.STATUS, type);
        if ("".equals(context) || "".equals(status)) {
            return false;
        }
        conn.url("http://" + ip + context + status)
                .auth(getBasicAuth(type))
                .timeout(Integer.parseInt(SettingUtil.getProperty(SettingKey.TIMEOUT)));
        conn.run();
        int rc = conn.getResultCode();
        return rc == 200;
    }

    /**
     * 저장된 preset 을 CCTV 로 부터 조회 하고 성공할 경우 preset combobox 생성 메서드
     */
    public ArrayList<String> getPresetList(String ip, String type) {
        ArrayList<String> presetList = new ArrayList<>();
        URLConnection conn = new URLConnection();
        conn.url("http://" + ip + SettingUtil.getProperty(SettingKey.CONTEXT, type) + SettingUtil.getProperty(SettingKey.PRESET, type))
                .auth(getBasicAuth(type));
        conn.run();
        int rc = conn.getResultCode();
        if (rc == 200) {
            LOGGER.info("[Request] Camera IP: {}, Control type: {}", ip, "GET_PRESETS");
            LOGGER.info("[Response] Result Code : {}", rc);
            String result = conn.getResult();
            if (result != null) {
                for (int i = 1, n = 11; i < n; i++) {
                    String presetValidStr = "root.PRESET.P" + (i - 1) + ".valid=";
                    if (result.contains(presetValidStr)) {
                        int presetValidIndex = result.indexOf(presetValidStr) + presetValidStr.length();
                        String valid = result.substring(presetValidIndex, presetValidIndex + 1);
                        if ("1".equals(valid)) {
                            presetList.add(i + "-saved");
                        } else {
                            presetList.add(String.valueOf(i));
                        }
                    }
                }
            }
        }
        return presetList;
    }

    /**
     * 카메라 제어 메서드 (팬, 틸트, 줌, 와이퍼)
     */
    public boolean control(String ip, Control control, int speed, String type) {
        URLConnection conn = new URLConnection();
        String url = "http://" + ip + SettingUtil.getProperty(SettingKey.CONTEXT, type) + SettingUtil.getProperty(SettingKey.CONTROL, type) + control.type();
        if (control == Control.WIPER) {
            url += SettingUtil.getProperty(SettingKey.PRESET, type) + 1;
        } else {
            url += "&PTZCTRL.speed=" + speed;
        }
        conn.url(url).auth(getBasicAuth(type));
        conn.run();
        int rc = conn.getResultCode();
        if (!"STOP".equals(control.name())) {
            LOGGER.info("[Request] Camera IP: {}, Control type: {}", ip, control.name());
            LOGGER.info("[Response] Result Code : {}", rc);
        }
        return rc == 200;
    }

    /**
     * 프리셋 이벤트 메서드 (이동, 저장, 삭제)
     */
    public boolean preset(String ip, Control control, int presetNo, String type) {
        URLConnection conn = new URLConnection();
        conn.url("http://" + ip + SettingUtil.getProperty(SettingKey.CONTEXT, type) +
                        SettingUtil.getProperty(SettingKey.CONTROL, type) + control.type() + "&PTZCTRL.no=" + presetNo)
                .auth(getBasicAuth(type));
        conn.run();
        int rc = conn.getResultCode();
        LOGGER.info("[Request] Camera IP: {}, Control type: {}, Preset No: {}", ip, control.name(), presetNo);
        LOGGER.info("[Response] Result Code : {}", rc);
        return rc == 200;
    }
}