package com.supcon.ses.DataUploadProcessor.repository;

import com.supcon.ses.DataUploadProcessor.model.pojo.AlarmPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class AlarmPointJdbcTemplateRepository {

    private final JdbcTemplate jdbcTemplate;

    public AlarmPointJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> findAllMap(Long cid) {
        Assert.notNull(cid,"cid can not be null.");
        String sql = "SELECT * FROM SESHA_QUOTAS_CONFIG WHERE VALID = 1 AND CID = ?";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, cid);
        return maps;
    }

    public void batchUpdate(List<AlarmPoint> dataList) {
        String sql = "UPDATE SESHA_QUOTAS_CONFIG SET TAG_VALUE=? WHERE QUOTA_ID=?";
        int[] updatedRows = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                AlarmPoint data = dataList.get(i);
                ps.setFloat(1, data.getTAG_VALUE());
                ps.setString(2, data.getQUOTA_ID());
            }

            @Override
            public int getBatchSize() {
                return dataList.size();
            }
        });
        log.error("Batch updated " + updatedRows.length + " rows.");
    }

    /*-----------------------------------------公共方法---------------------------------------------------*/

    private static AlarmPoint mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new AlarmPoint(rs.getString("QUOTA_ID"),
                rs.getString("TAG_NAME"),
                rs.getFloat("TAG_VALUE"),
                rs.getInt("VALID"));
    }

}
