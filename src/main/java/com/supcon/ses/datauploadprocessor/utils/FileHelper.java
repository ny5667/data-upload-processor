package com.supcon.ses.datauploadprocessor.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHelper {

    private static final Logger log = LoggerFactory.getLogger(FileHelper.class);


    public static String readFileContent(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            log.error("请指定读取文件的路径信息!");
            return null;
        } else {
            log.info("待操作文件地址：{}", filePath);
            File file = new File(filePath);
            if (!file.exists()) {
                log.error("待操作文件不存在, 请检查路径信息是否正确!");
                return null;
            } else {
                try {
                    BufferedReader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8);
                    Throwable var3 = null;

                    try {
                        StringBuffer buffer = new StringBuffer();

                        String line;
                        while((line = reader.readLine()) != null) {
                            buffer.append(line);
                        }

                        String var6 = buffer.toString().trim();
                        return var6;
                    } catch (Throwable var16) {
                        var3 = var16;
                        throw var16;
                    } finally {
                        if (reader != null) {
                            if (var3 != null) {
                                try {
                                    reader.close();
                                } catch (Throwable var15) {
                                    var3.addSuppressed(var15);
                                }
                            } else {
                                reader.close();
                            }
                        }

                    }
                } catch (Exception var18) {
                    log.error("读取文件[{}]内容出错：{}", filePath, var18);
                    return null;
                }
            }
        }
    }

}
