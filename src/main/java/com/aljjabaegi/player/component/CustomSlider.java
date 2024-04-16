package com.aljjabaegi.player.component;

import javax.swing.*;

/**
 * PTZ 속도 조절 slider
 *
 * @author GEON LEE
 * @since 2024-01-24
 */
public class CustomSlider extends JSlider {

    public CustomSlider(int orientation, int min, int max, int value) {
        super(orientation, min, max, value);
        initGUI();
    }

    private void initGUI() {
        this.setMajorTickSpacing(20);
        this.setMinorTickSpacing(5);
        this.setPaintTicks(true);
        this.setPaintLabels(true);
        this.setFocusable(false);
        this.setEnabled(false);
    }

    public void addValue(int value) {
        this.setValue(this.getValue() + value);
    }
}
