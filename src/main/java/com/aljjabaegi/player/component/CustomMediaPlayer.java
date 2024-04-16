package com.aljjabaegi.player.component;

import com.aljjabaegi.player.util.CommonVariable;
import com.aljjabaegi.player.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.fullscreen.adaptive.AdaptiveFullScreenStrategy;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * CustomMediaPlayer
 *
 * @author GEON LEE
 * @apiNote 2024-01-24 GEON LEE - Main Frame 에서 분리
 * @since 2024-01-24
 */
public class CustomMediaPlayer extends EmbeddedMediaPlayerComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomMediaPlayer.class);
    private final JFrame container;
    private final EmbeddedMediaPlayer player;
    private final JLabel initialImageLabel;

    private final JSplitPane splitPane;

    public CustomMediaPlayer(JFrame container, JSplitPane splitPane) {
        this.container = container;
        this.splitPane = splitPane;
        this.player = this.mediaPlayer();
        this.initialImageLabel = new JLabel(Utils.getImage("image/geonmoticon.png"));
        this.splitPane.setRightComponent(this.initialImageLabel);
        super.videoSurfaceComponent().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                /*EmbeddedMediaPlayerComponent 클릭 시 자체 포커스 때문에 Main Frame 이벤트 가 동작 하지 않기 때문에
                 * 클릭 시 Main Frame 으로 포커스 강제 이동 이벤트 추가*/
                container.requestFocus();
            }
        });
        // add default full screen strategy, setFullScreenStrategy 로 변경 가능
        this.player.fullScreen().strategy(new AdaptiveFullScreenStrategy(container));

        // add default error event
        this.player.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void error(MediaPlayer mediaPlayer) {
                String streamingAddr = mediaPlayer.media().info().mrl();
                LOGGER.info("[Response] RTSP Streaming : {}, Status: {}", streamingAddr, mediaPlayer.media().info().state());
                Utils.showAlertDialog(container, "경고", "RTSP 영상 정보가 유효하지 않습니다.", JOptionPane.ERROR_MESSAGE, null);
                stop();
            }
        });
    }

    /**
     * 영상이 출력 중인지 확인 메서드
     */
    public boolean isPlaying() {
        return this.mediaPlayer().status().isPlaying();
    }

    /**
     * 영상 stop 메서드
     */
    public void stop() {
        this.player.controls().stop();
    }

    /**
     * 영상 play 메서드
     */
    public void play(String rtspAddress) {
        if (CommonVariable.rtspPattern.matcher(rtspAddress).matches()) {
            if (this.splitPane.getRightComponent() instanceof JLabel) {
                this.splitPane.setRightComponent(this);
            }
            this.player.media().prepare(rtspAddress);
            this.player.controls().play();
        } else {
            reset();
            LOGGER.error("Invalid RTSP address: {}", rtspAddress);
            Utils.showAlertDialog(this.container, "경고",
                    "RTSP 주소가 올바르지 않습니다.", JOptionPane.ERROR_MESSAGE, null);
        }
    }

    /**
     * 영상 play 영역 초기화
     */
    public void reset() {
        this.splitPane.setRightComponent(this.initialImageLabel);
    }

    /**
     * 영상 Full screen 정책 추가 메서드
     */
    public void setFullScreenStrategy(AdaptiveFullScreenStrategy strategy) {
        this.player.fullScreen().strategy(strategy);
    }

    /**
     * 영상 이벤트 추가 메서드
     */
    public void addEventListener(MediaPlayerEventAdapter eventAdapter) {
        this.player.events().addMediaPlayerEventListener(eventAdapter);
    }
}