package com.aljjabaegi.player.service.code.record;

import lombok.Builder;

/**
 * code 정보 record
 */
@Builder
public record CodeDto(
        String codeId,
        String codeName) {
}
