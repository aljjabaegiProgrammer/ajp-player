package com.aljjabaegi.player.service.cctv;

import com.aljjabaegi.player.service.cctv.record.CctvDto;
import com.aljjabaegi.player.util.connection.DBConnection;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CCTV 정보 조회 Service
 *
 * @author GEON LEE
 * @since 2024-01-02
 */
@Service
public class CctvService {
    public List<CctvDto> getCctvList() {
        DBConnection dbConnection = new DBConnection();
        return dbConnection.getCCTVList();
    }
}
