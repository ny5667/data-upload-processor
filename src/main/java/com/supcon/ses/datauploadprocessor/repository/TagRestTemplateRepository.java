package com.supcon.ses.datauploadprocessor.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supcon.ses.datauploadprocessor.model.vo.ResultVO;
import com.supcon.ses.datauploadprocessor.model.vo.TagVo;
import com.supcon.ses.datauploadprocessor.utils.RestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class TagRestTemplateRepository {

    private final String baseUrl = "http://%s:%s";

    private final String url = "/msService/public/TagManagement/readTagsSync";

    private final ObjectMapper objectMapper;

    private final RestTemplateUtils restTemplateUtils;

    public TagRestTemplateRepository(ObjectMapper objectMapper, RestTemplateUtils restTemplateUtils) {
        this.objectMapper = objectMapper;
        this.restTemplateUtils = restTemplateUtils;
    }

    public List<TagVo> findAll(String ip, String port, List<String> tagNames) throws JsonProcessingException {
        String baseUrlF = String.format(baseUrl, ip, port);
        Map<String, Object> uriVariables = new HashMap<>();
//        uriVariables.put("id", "123");
//        Student student = new Student("Jack", 20);
        ResponseEntity<String> response = null;
        log.error("调用测试接口参数");
        String s = objectMapper.writeValueAsString(tagNames);
        log.error(s);

        Map<String, Object> request = new HashMap<String, Object>();
        request.put("tagNames", tagNames);

        try {
            response = restTemplateUtils.post(baseUrlF, url, request, String.class, uriVariables);
            log.error("调用测点接口返回");
            String body = response.getBody();
            log.error(body);
        } catch (Exception ex) {
            log.error("调用测试接口报错", ex);
            return Collections.emptyList();
        }

        ResultVO resultVO = objectMapper.readValue(response.getBody(), ResultVO.class);
        List<TagVo> tagVos = objectMapper.readValue(objectMapper.writeValueAsString(resultVO.getData()), new TypeReference<List<TagVo>>() {
        });

        return tagVos;

    }

}
