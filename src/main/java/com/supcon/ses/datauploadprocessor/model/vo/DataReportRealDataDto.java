package com.supcon.ses.datauploadprocessor.model.vo;

import lombok.Data;

@Data
public class DataReportRealDataDto {

    /**
     * 指标编码，由系统下发
     */
    private String quotaId;
    /**
     * 指标当前采集值
     */
    private float value;

    public DataReportRealDataDto(String quotaId, float value) {
        this.quotaId = quotaId;
        this.value = value;
    }

}
