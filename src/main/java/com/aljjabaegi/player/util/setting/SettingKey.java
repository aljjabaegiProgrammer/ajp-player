package com.aljjabaegi.player.util.setting;

import java.util.ArrayList;
import java.util.List;

/**
 * setting key enum class
 *
 * @author GEON LEE
 * @since 2024-02-02
 */
public enum SettingKey {
    URL("url", "DB", ""), USERNAME("username", "DB", ""), PASSWORD("password", "DB", ""),
    TIMEOUT("timeout", "CONNECTION", "200"), DIVISION("division", "MULTI", "4"),
    PORT("port", "CCTV", "554"), CHANNEL("channel", "CCTV", "0"),
    ID("id", "CCTV", "admin"), PW("pw", "CCTV", ""),
    CONTEXT("onvif.context", "CCTV", "/cgi-bin"),
    STATUS("onvif.status", "CCTV", "/devInfo.cgi?action=list&group=STATUS"),
    CONTROL("onvif.control", "CCTV", "/control.cgi?action=update&group=PTZCTRL&channel=0&PTZCTRL.action="),
    PRESET("onvif.preset", "CCTV", "/param.cgi?action=list&group=PRES");

    private final String value;
    private final String type;

    private final String defaultValue;

    SettingKey(String value, String type, String defaultValue) {
        this.value = value;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public static List<String> valuesByType(String type) {
        List<String> valueList = new ArrayList<>();
        for (SettingKey setting : SettingKey.values()) {
            if (setting.type.equals(type)) {
                valueList.add(setting.value());
            }
        }
        return valueList;
    }

    public String value() {
        return this.value;
    }

    public String type() {
        return this.type;
    }

    public String defaultValue() {
        return this.defaultValue;
    }
}
