package com.aljjabaegi.player.service.cctv.record;

import lombok.Builder;

/**
 * CCTV 데이터 조회용 DTO
 *
 * @author GEONLEE
 * @since 2024-01-02
 */
@Builder
public record CctvDto(
        String cctvId,
        String cctvName,
        String ip,
        String streamingAddress,
        String cctvType,
        String cctvTypeName) {

}
