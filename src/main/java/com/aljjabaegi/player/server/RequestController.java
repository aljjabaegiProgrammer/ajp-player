package com.aljjabaegi.player.server;

import com.aljjabaegi.player.component.popup.MultiMediaPlayer;
import com.aljjabaegi.player.server.record.MultiPlayRequest;
import com.aljjabaegi.player.server.record.MultiPlayResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author GEONLEE
 * @since 2024-04-30
 */
@RestController
public class RequestController {

    @PostMapping(value = "/open-multi-player", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MultiPlayResponse> openMultiPlayer(@RequestBody MultiPlayRequest multiPlayRequest) {
        MultiMediaPlayer multiMediaPlayer = new MultiMediaPlayer(multiPlayRequest);
        multiMediaPlayer.setVisible(true);
        return ResponseEntity.ok().body(
                MultiPlayResponse
                        .builder()
                        .status(200)
                        .message("Success")
                        .build());
    }
}
