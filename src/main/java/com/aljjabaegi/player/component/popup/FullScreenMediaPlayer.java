package com.aljjabaegi.player.component.popup;

import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 영상 전체 화면 표출용 JFrame
 *
 * @author GEONLEE
 * @since 2024-05-02
 */
public class FullScreenMediaPlayer extends JFrame {

    private final EmbeddedMediaPlayer player;

    public FullScreenMediaPlayer() {
        super();
        EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        this.player = mediaPlayerComponent.mediaPlayer();
        add(mediaPlayerComponent);
        mediaPlayerComponent.videoSurfaceComponent().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                /*EmbeddedMediaPlayerComponent 클릭 시 자체 포커스 때문에 Main Frame 이벤트 가 동작 하지 않기 때문에
                 * 클릭 시 Main Frame 으로 포커스 강제 이동 이벤트 추가*/
                requestFocus();
            }
        });
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    player.controls().stop();
                    setVisible(false);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public void play(String rtspAddress) {
        setVisible(true);
        this.player.media().prepare(rtspAddress);
        this.player.controls().play();
    }
}
