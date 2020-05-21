package com.atguigu.gulimall.thirdparty;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectRequest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

/**
 * Description
 * OssFileUploadTest
 * <p>
 * Data
 * 2020/5/17-16:50
 *
 * @author zrx
 * @version 1.0
 */

@SpringBootTest
public class OssFileUploadTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(OssFileUploadTest.class);

    @Autowired
    private OSSClient ossClient;

    @Value("${spring.cloud.alicloud.oss.bucket-name}")
    private String bucketName;

    private String folderName = "product-brand";


    @Test
    public void testFileUpload(){
//        PutObjectRequest putObjectRequest = new PutObjectRequest(
//                bucketName,
//                folderName+"/csdn.jpg",
//                new File("C:\\Users\\javalearn\\Documents\\github\\grainmall2020\\grainmall_backend\\docs\\product-brand\\csdn.jpg")
//        );
//
//        ossClient.putObject(putObjectRequest);
//
//        ossClient.shutdown();

        LOGGER.info("文件上传测试成功");
    }
}
