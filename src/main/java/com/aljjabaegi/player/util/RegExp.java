package com.aljjabaegi.player.util;

/**
 * @author GEONLEE
 * @apiNote 정규식 설정 class <br/>
 * 2023.09.01 jisu ONLY_ENG 추가 <br/>
 * 2023.11.22 mijin REMOVE_BLANK 추가 <br/>
 * @since 2023-03-06
 */
public class RegExp {
    public static final String ONLY_NUMBER = "[^0-9]+";

    public static final String ONLY_ENG = "[^A-Za-z]+";

    // 공백 제거
    public static final String REMOVE_BLANK = "[^가-힣a-zA-Z0-9\\\\s]";

    public static final String IP = "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b|\\b(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}\\b";

    public static final String RTSP_ADDRESS = "rtsp:\\/\\/(?:\\S+:\\S+@)?(?:[a-zA-Z0-9\\-\\.]+)(?::(?:\\d{1,5}))?(?:\\/\\S*)?$";

}
