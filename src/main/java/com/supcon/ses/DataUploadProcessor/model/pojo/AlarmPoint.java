package com.supcon.ses.DataUploadProcessor.model.pojo;

import lombok.Data;

@Data
public class AlarmPoint {

    /**
     * 主键
     */
    private String QUOTA_ID;

    /**
     * 位号名称
     */
    private String TAG_NAME;

    /**
     * 实时数据
     */
    private Float TAG_VALUE;

    /**
     * 数据是否有效
     */
    private int VALID;

    public AlarmPoint() {
    }

    public AlarmPoint(String QUOTA_ID, String TAG_NAME, Float TAG_VALUE, int VALID) {
        this.QUOTA_ID = QUOTA_ID;
        this.TAG_NAME = TAG_NAME;
        this.TAG_VALUE = TAG_VALUE;
        this.VALID = VALID;
    }

    public AlarmPoint(String QUOTA_ID, Float TAG_VALUE) {
        this.QUOTA_ID = QUOTA_ID;
        this.TAG_VALUE = TAG_VALUE;
    }

}
