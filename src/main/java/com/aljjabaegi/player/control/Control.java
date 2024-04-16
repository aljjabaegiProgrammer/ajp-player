package com.aljjabaegi.player.control;

/**
 * CCTV 제어 제약용 Enum class
 *
 * @author GEON LEE
 * @apiNote 2024-01-22 GEON LEE - WIPER 추가
 * 2024-01-24 GEON LEE - SPEED_UP, SPEED_DOWN 제거
 * @since 2024-01-02
 */
public enum Control {
    STOP(0), LEFT(2), LEFT_UP(6), LEFT_DOWN(7), RIGHT(3), RIGHT_UP(8), RIGHT_DOWN(9), UP(4), DOWN(5),
    PRESET_SAVE(10), PRESET_MOVE(11), PRESET_DELETE(12),
    ZOOM_IN(13), ZOOM_OUT(14),
    WIPER(32);

    private final int type;

    Control(int type) {
        this.type = type;
    }

    public int type() {
        return this.type;
    }
}
