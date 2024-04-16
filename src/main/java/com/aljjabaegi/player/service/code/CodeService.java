package com.aljjabaegi.player.service.code;

import com.aljjabaegi.player.service.code.record.CodeDto;
import com.aljjabaegi.player.util.connection.DBConnection;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CodeService {

    public List<CodeDto> getCodeList() {
        DBConnection dbConnection = new DBConnection();
        return dbConnection.getCodeList();
    }
}
