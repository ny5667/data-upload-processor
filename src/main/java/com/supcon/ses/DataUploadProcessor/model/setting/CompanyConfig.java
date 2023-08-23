package com.supcon.ses.DataUploadProcessor.model.setting;

import com.supcon.ses.DataUploadProcessor.constant.DefaultSettingConstant;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class CompanyConfig {

    /**
     * adp平台企业cid
     */
    private String cid = DefaultSettingConstant.DEFAULT_CID;

    /**
     * 企业编号 必填
     */
    private String companyCode = StringUtils.EMPTY;

    /**
     * 企业名称 必填
     */
    private String companyName = StringUtils.EMPTY;

    /**
     * 接口认证appKey
     */
    private String appKey = StringUtils.EMPTY;

    /**
     * 接口认证appSecret
     */
    private String appSecret = StringUtils.EMPTY;

    /**
     * 上报接口认证appId
     */
    private String appId = StringUtils.EMPTY;

    /**
     * 加密密钥及加密向量
     */
    private String encryptionKey = StringUtils.EMPTY;

    /**
     * 企业编码
     */
    private String enterpriseId = StringUtils.EMPTY;

    /**
     * 服务器地址
     */
    private String serverAddress = StringUtils.EMPTY;

    /**
     * 网关编码
     */
    private String gatewayId = StringUtils.EMPTY;

    /**
     * TCP Socket的IP
     */
    private String socketIp = StringUtils.EMPTY;

    /**
     * TCP Socket的端口
     */
    private String socketPort = StringUtils.EMPTY;

    /**
     * AES加密密匙
     */
    private String secretKey = StringUtils.EMPTY;

    /**
     * AES加密偏移量
     */
    private String offset = StringUtils.EMPTY;

    /**
     * ADP平台的IP
     */
    private String adpServerIp = StringUtils.EMPTY;

    /**
     * ADP平台的端口
     */
    private String adpServerPort = StringUtils.EMPTY;

    /**
     * 上报一个批次的数量
     */
    private String bitchSize = StringUtils.EMPTY;


}