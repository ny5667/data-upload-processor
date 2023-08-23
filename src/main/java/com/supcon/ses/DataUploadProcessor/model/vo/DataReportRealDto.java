package com.supcon.ses.DataUploadProcessor.model.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DataReportRealDto {

    /**
     * 网关编码
     */
    private String gatewayId;

    /**
     * 时间戳，格式 yyyyMMddHHmmss
     */
    private String collectTime;

    /**
     * 数据源连通性，true 表示数据源连 通正常，数据有效；false 表示数据 源连通异常，数据无效
     */
    private boolean isConnectDataSource;

    /**
     * 报文类型，report 表示实时报文； continues 表示断点续传的报文。
     */
    private String reportType;

    /**
     * 调用者定义的数据包ID，同一个数据包必须具有相同且全局唯一的ID。服务使用本字段数据判断是否传递了重复的包。建议使用UUID
     */
    private String dataId;

    /**
     * 企业编码
     */
    private String enterpriseId;

    /**
     * 指标数据集合
     */
    private List<Map<String, Object>> datas;

    public DataReportRealDto(String dataId, String enterpriseId, String gatewayId, String collectTime, boolean isConnectDataSource, String reportType,List<Map<String, Object>> datas) {
        this.dataId = dataId;
        this.enterpriseId = enterpriseId;
        this.gatewayId = gatewayId;
        this.collectTime = collectTime;
        this.isConnectDataSource = isConnectDataSource;
        this.reportType = reportType;
        this.datas = datas;
    }

}
