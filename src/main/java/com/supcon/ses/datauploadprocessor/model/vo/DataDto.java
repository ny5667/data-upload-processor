package com.supcon.ses.datauploadprocessor.model.vo;

import lombok.Data;

@Data
public class DataDto {

    /**
     * appId，由系统下发
     */
    private String appId;

    /**
     * 服务 id，由系统下发
     */
    private String serviceId;

    /**
     * dataId,由调用方生成与 data 一一对应，应 答时会携带该字段
     */
    private String dataId;

    /**
     * 实时消息，传输时需要加密，使用 AES 算法 进行加密，AES 密钥由系统下发
     */
    private String data;

    public DataDto(String appId, String serviceId, String dataId, String data){
        this.appId = appId;
        this.serviceId = serviceId;
        this.dataId = dataId;
        this.data = data;
    }

}
