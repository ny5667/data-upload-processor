package com.supcon.ses.datauploadprocessor.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supcon.ses.datauploadprocessor.cache.DataUploadSettingCache_RealData;
import com.supcon.ses.datauploadprocessor.constant.DefaultSettingConstant;
import com.supcon.ses.datauploadprocessor.model.pojo.AlarmPoint;
import com.supcon.ses.datauploadprocessor.model.setting.CompanyConfig;
import com.supcon.ses.datauploadprocessor.model.vo.DataDto;
import com.supcon.ses.datauploadprocessor.model.vo.DataReportRealDto;
import com.supcon.ses.datauploadprocessor.model.vo.TagVo;
import com.supcon.ses.datauploadprocessor.repository.AlarmPointJdbcTemplateRepository;
import com.supcon.ses.datauploadprocessor.repository.TagRestTemplateRepository;
import com.supcon.ses.datauploadprocessor.utils.Aes2Util;
import com.supcon.ses.datauploadprocessor.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
public class AlarmPointUpload {

    private static final Logger log = LoggerFactory.getLogger(AlarmPointUpload.class);

    private final List<String> ignoreColumnList = Arrays.asList("BIZ_ID", "TAG_NAME", "TAG_VALUE", "VALID","CID","ALARM_NAME"); // 忽略的列名列表

    private SimpleDateFormat mySimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private final AlarmPointJdbcTemplateRepository repository;

    private final TagRestTemplateRepository tagRestTemplateRepository;

    private final ObjectMapper objectMapper;

    private final static Map<String, Float> value_map = new HashMap<>();

    static {
        value_map.put("false", 0f);
        value_map.put("true", 1f);
    }

    public AlarmPointUpload(JdbcTemplate jdbcTemplate, AlarmPointJdbcTemplateRepository repository, TagRestTemplateRepository tagRestTemplateRepository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.tagRestTemplateRepository = tagRestTemplateRepository;
        this.objectMapper = objectMapper;
    }


    @Scheduled(fixedRate = 5 * 1000)
    public void run() throws JsonProcessingException {

        log.error("实时数据上报开始.");

        if (DataUploadSettingCache_RealData.COMPANIES == null) {
            log.error("配置信息为空，跳过.");
            return;
        }

        for (CompanyConfig company :
                DataUploadSettingCache_RealData.COMPANIES) {
            uploadDataByCompany(company);
        }

    }


    /*-----------------------------------------公共方法---------------------------------------------------*/

    /**
     * 根据公司上报实时数据
     *
     * @param company 公司信息
     * @throws JsonProcessingException
     */
    private void uploadDataByCompany(CompanyConfig company) throws JsonProcessingException {
        //查询实时数据
        List<Map<String, Object>> allAlarmPoint = repository.findAllMap(Long.parseLong(company.getCid()));

        //查询位号数据
        String ip = company.getAdpServerIp();
        String port = company.getAdpServerPort();
        List<String> tagNames = allAlarmPoint.stream().filter(v -> v.get("TAG_NAME") != null).map(v -> v.get("TAG_NAME").toString()).collect(Collectors.toList());
        if (tagNames.isEmpty()) {
            log.error("无实时上报数据.");
            return;
        }
        List<TagVo> tagVoList = tagRestTemplateRepository.findAll(ip, port, tagNames);

        //设置实时数据位号值
        setTagValueAndDefaultValue(allAlarmPoint, tagVoList);

        //去掉不需要上报的字段
        allAlarmPoint.stream()
                .forEach(map -> ignoreColumnList.forEach(ignoreColumn -> map.computeIfPresent(ignoreColumn, (key, value) -> null)));

        //处理上报数据/加密
        int batchSize = Integer.parseInt(company.getBitchSize());  // 每批次的大小
        for (int i = 0; i < allAlarmPoint.size(); i += batchSize) {
            List<Map<String, Object>> batch = allAlarmPoint.subList(i, Math.min(i + batchSize, allAlarmPoint.size()));
            uploadData(batch, company);
        }
        // 更新位号数据到数据库中
        // 如果下次测试数据取不到则用上一次实时数据库的值
        List<AlarmPoint> dataList = new ArrayList<>();
        allAlarmPoint.forEach(c -> {
            String quotaId = (String) c.get("QUOTA_ID");
            Float value = (Float) c.get(DefaultSettingConstant.TAG_VALUE);
            AlarmPoint po = new AlarmPoint(quotaId, value);
            dataList.add(po);
        });
        repository.batchUpdate(dataList);
        log.error("实时数据上报结束.");
    }

    /**
     * 上报数据
     *
     * @param batch   上报数据信息
     * @param company 公司信息
     */
    private void uploadData(List<Map<String, Object>> batch, CompanyConfig company) throws JsonProcessingException {

        String collectTime = mySimpleDateFormat.format(new Date());
        DataReportRealDto dataReportRealDto = new DataReportRealDto(
                UUID.randomUUID().toString(),
                company.getEnterpriseId(),
                company.getEnterpriseId(),
                collectTime,
                true,
                DefaultSettingConstant.REAL_DATA,
                batch
        );

        String s = JsonHelper.writeValue(dataReportRealDto);
        log.error(">>>>发送list数据 {}", s);

        String sendData = Aes2Util.encryptCBC(
                s,
                company.getSecretKey(),
                company.getOffset());

        DataDto dataDto = new DataDto(
                company.getAppId(),
                DefaultSettingConstant.REAL_DATA,
                UUID.randomUUID().toString(),
                sendData
        );

        String socketIp = company.getSocketIp();
        String socketPort = company.getSocketPort();

        String sendJson = JsonHelper.writeValue(dataDto);
        log.error("发送报警消息.");
        log.error(sendJson);

        try {
            //发送报文
            Socket socket = new Socket(socketIp, Integer.parseInt(socketPort));
            getStringBuilder(socket, sendJson);
        } catch (Exception e) {
            log.error("报警消息发送失败", e);
        }

    }

    private static void getStringBuilder(Socket socket, String sendJson) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bufferedWriter.write(sendJson);
        bufferedWriter.write("@@");
        bufferedWriter.flush();

        // 接收报文
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

        StringBuilder stringBuilder = new StringBuilder();
        String line = "";
        while((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        log.error(">>>>发送返回报文 {}", stringBuilder);
    }


    /**
     * 设置报警点的位号值和其他默认值
     *
     * @param allAlarmPoint 实时数据
     * @param tagVoList     位号列表
     */
    private void setTagValueAndDefaultValue(List<Map<String, Object>> allAlarmPoint, List<TagVo> tagVoList) {
        Map<String, TagVo> tagVoMap = tagVoList.stream()
                .collect(Collectors.toMap(TagVo::getName, Function.identity(), (v1, v2) -> v1));
        allAlarmPoint.forEach(c -> {
            if (c.get("TAG_NAME") == null) {
                return;
            }
            String tagName = c.get("TAG_NAME").toString();
            TagVo tagVo = tagVoMap.get(tagName);
            if (tagVo == null || tagVo.getValue() == null || tagVo.getValue().isEmpty()) {
                if (c.get("TAG_VALUE") == null) {
                    c.put(DefaultSettingConstant.TAG_VALUE, 0f);//默认设置为0
                } else {
                    String tagValue = c.get("TAG_VALUE").toString();
                    float v = Float.parseFloat(tagValue);
                    c.put(DefaultSettingConstant.TAG_VALUE, v);//设置为上一次查询出的位号值
                }
                return;
            }
            log.error("测点取到值为：");
            log.error(tagVo.getValue());
            //如果是bool类型，则转成0/1类型
            Float boolString = value_map.get(tagVo.getValue());
            log.error("map值为：{}", boolString);
            if (boolString != null) {
                c.put(DefaultSettingConstant.TAG_VALUE, boolString);//位号实时值
                return;
            }
            c.put(DefaultSettingConstant.TAG_VALUE, Float.parseFloat(tagVo.getValue()));//位号实时值
        });

    }

}
