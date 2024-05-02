package com.aljjabaegi.player.server.record;

import lombok.Builder;

/**
 * @author GEONLEE
 * @since 2024-04-30
 */
@Builder
public record MultiPlayResponse(
        int status,
        String message) {
}
