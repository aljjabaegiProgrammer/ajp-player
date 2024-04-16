package com.aljjabaegi.player.util;

import org.springframework.core.io.ClassPathResource;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * @author GEON LEE
 * @apiNote 2024-01-26 GEON LEE - showAlertDialog, showConfirmDialog 추가, MAIN FRAME 에서 분리
 * @since 2024-01-22
 */
public class Utils {
    /**
     * 리소스 이미지로 아이콘을 생성하여 리턴하는 메서드
     */
    public static ImageIcon getImage(String path) {
        ClassPathResource resource = new ClassPathResource(path);
        try {
            return new ImageIcon(resource.getContentAsByteArray());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Alert, message dialog 출력 메서드
     */
    public static void showAlertDialog(Container container, String title, Object msg, int msgOption, Icon icon) {
        /*showMessageDialog 의 UI 업데이트 하는 부분이 다른 스레드에서 실행되어, invokeLater를 하지 않을 경우
         * 특정 이벤트 내에서 해당 메서드를 호출하게 할 경우 오류가 발생할 수 있음.
         * */
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(container, msg, title, msgOption, icon);
        });
    }

    /**
     * Confirm, confirm dialog 출력 메서드
     */
    public static boolean showConfirmDialog(Container container, String msg) {
        int result = JOptionPane.showConfirmDialog(container, msg, "확인", JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    /**
     * 첫 글자 대문자로 리턴
     */
    public static String upperCaseFirst(String val) {
        char[] arr = val.toCharArray();
        arr[0] = Character.toUpperCase(arr[0]);
        return new String(arr);
    }
}
