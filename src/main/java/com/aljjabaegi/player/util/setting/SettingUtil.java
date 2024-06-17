package com.aljjabaegi.player.util.setting;

import com.aljjabaegi.player.service.code.record.CodeDto;
import com.aljjabaegi.player.util.CommonVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SettingUtil {
    public static final Properties settingProperties = new Properties();
    public static final Set<String> controllableType = Set.of("01", "03");
    public static final Map<String, String> cctvTypeMap = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingUtil.class);
    private static File settingFile;

    /**
     * 세팅 파일 체크 후 없으면 생성, 있으면 로드 하는 메서드
     */
    public static void checkSettingFiles() {
        LOGGER.info("Check setting file...");
        Path userHomeDirectory = Paths.get(System.getProperty("user.home"));
        Path settingPath = userHomeDirectory.resolve(CommonVariable.settingDirectoryName);
        try {
            Files.createDirectories(settingPath);
            settingFile = new File(settingPath + File.separator + "setting.properties");
            if (!settingFile.exists()) {
                boolean result = settingFile.createNewFile();
                if (result) {
                    LOGGER.info("New settings file creation completed.");
                    createSettingData(settingFile);
                } else {
                    LOGGER.error("New settings file creation fail.");
                }
            } else {
                try (FileInputStream fis = new FileInputStream(settingFile)) {
                    settingProperties.load(fis);
                    LOGGER.info("Settings file load completed.");
                } catch (IOException e) {
                    LOGGER.error("Setting file load fail.", e);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Settings folder and file creation fail.");
        }
    }

    /**
     * properties 값 리턴
     */
    public static String getProperty(SettingKey key, String type) {
        return settingProperties.getProperty(type + "." + key.value());
    }

    /**
     * properties 값 리턴
     */
    public static String getProperty(SettingKey key) {
        return settingProperties.getProperty(key.value());
    }

    public static Set<Object> getPropertyKeys() {
        return settingProperties.keySet();
    }

    /**
     * property value 세팅 메서드
     */
    public static void setProperty(String key, String value) {
        settingProperties.setProperty(key, value);
    }

    /**
     * property value 를 setting file 에 저장 하는 메서드
     */
    public static void updateProperties() {
        try (OutputStream outputStream = new FileOutputStream(settingFile)) {
            settingProperties.store(outputStream, "Update Properties File");
        } catch (IOException e) {
            LOGGER.error("setting file update fail.");
        }
    }

    /**
     * 세팅 데이터 초기 값 생성 메서드
     */
    private static void createSettingData(File settingFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(settingFile))) {
            writer.write(SettingKey.TIMEOUT.value() + "=" + SettingKey.TIMEOUT.defaultValue());
            writer.newLine();
            writer.write(SettingKey.DIVISION.value() + "=" + SettingKey.DIVISION.defaultValue());
            writer.newLine();
            writer.write(SettingKey.URL.value() + "=");
            writer.newLine();
            writer.write(SettingKey.USERNAME.value() + "=");
            writer.newLine();
            writer.write(SettingKey.PASSWORD.value() + "=");
            writer.newLine();

            Set<String> keys = cctvTypeMap.keySet();
            for (String key : keys) {
                for (String value : SettingKey.valuesByType("CCTV")) {
                    writer.write(key + "." + value + "=" + getDefaultValue(value));
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            LOGGER.error("setting 파일 생성에 실패 하였습니다.");
        }
        try (FileInputStream fis = new FileInputStream(settingFile)) {
            settingProperties.load(fis);
            LOGGER.info("Settings file load completed.");
        } catch (IOException e) {
            LOGGER.error("Setting file load fail.", e);
        }
    }

    private static String getDefaultValue(String value) {
        switch (value) {
            case "port" -> {
                return SettingKey.PORT.defaultValue();
            }
            case "channel" -> {
                return SettingKey.CHANNEL.defaultValue();
            }
            case "onvif.context" -> {
                return SettingKey.CONTEXT.defaultValue();
            }
            case "onvif.status" -> {
                return SettingKey.STATUS.defaultValue();
            }
            case "onvif.control" -> {
                return SettingKey.CONTROL.defaultValue();
            }
            case "onvif.preset" -> {
                return SettingKey.PRESET.defaultValue();
            }
        }
        return "";
    }

    /**
     * 코드 테이블에 저장된 코드 값으로 cctv type setting
     *
     * @author GEONLEE
     * @since 2024-06-17
     */
    public static void setCctvTypeMap(List<CodeDto> list) {
        cctvTypeMap.clear();
        list.forEach(cctvDto -> {
            cctvTypeMap.put(cctvDto.codeId(), cctvDto.codeName());
        });
    }
}
