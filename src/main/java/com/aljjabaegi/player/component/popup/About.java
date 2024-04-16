package com.aljjabaegi.player.component.popup;

import com.aljjabaegi.player.util.Utils;

import javax.swing.*;
import java.awt.*;

/**
 * about popup component
 *
 * @author GEON LEE
 * @since 2024-01-22<br />
 * 2024-04-16 GEONLEE - Refactoring
 */
public class About {
    private final Container container;

    public About(Container container) {
        this.container = container;
    }

    public void show(String projectName, String projectVersion, String javaVersion, String springbootVersion) {
        Icon aboutIcon = Utils.getImage("image/geonmoticon-40.png");
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this.container, projectName + " (Version" + projectVersion +
                            ")\nJava " + javaVersion + "\nSpringboot " + springbootVersion +
                            "\n\nDeveloped By Geon Lee.\nCopyright © 2024 Corp. All rights reserved.",
                    "About", JOptionPane.INFORMATION_MESSAGE, aboutIcon);
        });
    }
}
