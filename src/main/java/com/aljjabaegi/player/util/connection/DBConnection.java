package com.aljjabaegi.player.util.connection;

import com.aljjabaegi.player.service.cctv.record.CctvDto;
import com.aljjabaegi.player.service.code.record.CodeDto;
import com.aljjabaegi.player.util.setting.SettingKey;
import com.aljjabaegi.player.util.setting.SettingUtil;
import com.tmax.tibero.jdbc.util.TokenMgrError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author GEONLEE
 * @since 2024-04-12
 */
public class DBConnection {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBConnection.class);
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public DBConnection() {
        String url = SettingUtil.getProperty(SettingKey.URL);
        String userName = SettingUtil.getProperty(SettingKey.USERNAME);
        String password = SettingUtil.getProperty(SettingKey.PASSWORD);
        if (StringUtils.hasText(url) && StringUtils.hasText(userName) && StringUtils.hasText(password)) {
            try {
                Class.forName("com.tmax.tibero.jdbc.TbDriver");
                this.connection = DriverManager.getConnection(url, userName, password);
                this.preparedStatement = null;
            } catch (ClassNotFoundException | SQLException | TokenMgrError e) {
                throw new RuntimeException(e);
            }
        }
    }

    public DBConnection(String url, String userName, String password) {
        if (StringUtils.hasText(url) && StringUtils.hasText(userName) && StringUtils.hasText(password)) {
            try {
                Class.forName("com.tmax.tibero.jdbc.TbDriver");
                this.connection = DriverManager.getConnection(url, userName, password);
                this.preparedStatement = null;
            } catch (ClassNotFoundException | SQLException | TokenMgrError e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean test() throws SQLException {
        this.preparedStatement = this.connection.prepareStatement(
                "select 1 from dual");
        this.resultSet = this.preparedStatement.executeQuery();
        return this.resultSet.next();
    }

    public List<CctvDto> getCCTVList() {
        if (this.connection != null) {
            try {
                this.preparedStatement = this.connection.prepareStatement("""
                        select t1.* 
                             , t2.cd_nm as CCTV_TYPE_NM 
                          from m_op_cctv t1 
                          left join m_op_cd t2 on (t2.grp_cd_id = 'CCTV_TYPE' and t2.cd_id = t1.cctv_type) 
                         where t1.use_yn = 'Y'
                        """);
                this.resultSet = this.preparedStatement.executeQuery();
                List<CctvDto> cctvList = new ArrayList<>();
                while (this.resultSet.next()) {
                    cctvList.add(CctvDto.builder()
                            .cctvId(this.resultSet.getString("CCTV_ID"))
                            .cctvName(this.resultSet.getString("CCTV_NM"))
                            .cctvType(this.resultSet.getString("CCTV_TYPE"))
                            .cctvTypeName(this.resultSet.getString("CCTV_TYPE_NM"))
                            .ip(this.resultSet.getString("COMM_IP"))
                            .streamingAddress(this.resultSet.getString("STRMNG_RTSP_ADDR"))
                            .build());
                }
                return cctvList;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    this.resultSet.close();
                    this.preparedStatement.close();
                    this.connection.close();
                } catch (SQLException e) {
                    LOGGER.error("connection close fail.");
                }
            }
        }
        return null;
    }

    public List<CodeDto> getCodeList() {
        if (this.connection != null) {
            try {
                this.preparedStatement = this.connection.prepareStatement(
                        "select cd_id, cd_nm from m_op_cd where grp_cd_id = 'CCTV_TYPE' and use_yn = 'Y'");
                this.resultSet = this.preparedStatement.executeQuery();
                List<CodeDto> codeList = new ArrayList<>();
                while (this.resultSet.next()) {
                    codeList.add(CodeDto.builder()
                            .codeId(this.resultSet.getString("CD_ID"))
                            .codeName(this.resultSet.getString("CD_NM"))
                            .build());
                }
                return codeList;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    this.resultSet.close();
                    this.preparedStatement.close();
                    this.connection.close();
                } catch (SQLException e) {
                    LOGGER.error("connection close fail.");
                }
            }
        }
        return null;
    }
}
