package com.aljjabaegi.player.server.record;

import java.util.List;

/**
 * @author GEONLEE
 * @since 2024-04-30
 */
public record MultiPlayRequest(
        List<String> cctvIds
) {
}
