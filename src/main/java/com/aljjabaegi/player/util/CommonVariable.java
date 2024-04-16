package com.aljjabaegi.player.util;

import com.aljjabaegi.player.PlayerApplication;
import com.aljjabaegi.player.component.CustomTable;
import com.aljjabaegi.player.component.CustomTitleBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;

/**
 * 공통 전역 변수 및 메서드 클래스
 *
 * @author GEON LEE
 * @apiNote 2024-01-30 GEON LEE - xMouse, yMouse, locationX, locationY 제거, CustomTitleBar 에서 관리
 * @since 2024-01-25
 */
public class CommonVariable {
    public static final String settingDirectoryName = ".ajp";
    public static CustomTable cctvTable; /* ip 로 검색 및 영상 play 하기 위해 테이블 전역 처리*/
    public static CustomTitleBar titleBar; /*title text 변경을 위해 title 처리*/
    public static Pattern ipPattern = Pattern.compile(RegExp.IP);
    public static Pattern rtspPattern = Pattern.compile(RegExp.RTSP_ADDRESS);

//    public static int setSelectionByIp(String ip, boolean autoPlay) {
//        int idx = -1;
//        int size = cctvTable.getRowCount();
//        if (size > 0) {
//            for (int i = 0; i < size; i++) {
//                /*model 의 getValueAt 은 초기 상태의 데이터를 가져오기 때문에 정렬이나 순서가 변경된 경우 정확한 값을 찾지 못함.*/
//                int rowIndex = CommonVariable.cctvTable.convertRowIndexToModel(i);
//                String cctvIp = (String) CommonVariable.cctvTable.getModel().getValueAt(rowIndex, 3);
//                if (ip.equals(cctvIp)) {
//                    idx = i;
//                    break;
//                }
//            }
//            if (idx != -1) {
//                PlayerApplication app = ApplicationContextHolder.getContext().getBean(PlayerApplication.class);
//                closeAllDialogs(app);
//                if (app.getExtendedState() == JFrame.ICONIFIED) {
//                    app.setExtendedState(JFrame.NORMAL);
//                }
//                CommonVariable.cctvTable.setRowSelectionInterval(idx, idx);
//                if (autoPlay) {
//                    MouseEvent clickEvent = new MouseEvent(
//                            CommonVariable.cctvTable,
//                            MouseEvent.MOUSE_CLICKED,
//                            System.currentTimeMillis(),
//                            0,
//                            0,
//                            0,
//                            1,
//                            false
//                    );
//                    CommonVariable.cctvTable.dispatchEvent(clickEvent);
//                }
//            }
//        }
//        return idx;
//    }

    /**
     * 열려 있는 모든 팝업 창 닫는 메서드
     */
    private static void closeAllDialogs(Frame frame) {
        Window[] windows = frame.getOwnedWindows();
        for (Window window : windows) {
            if (window instanceof Dialog dialog) {
                dialog.dispose();
            }
        }
    }
}
