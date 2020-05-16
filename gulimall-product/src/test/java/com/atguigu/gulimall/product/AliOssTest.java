package com.atguigu.gulimall.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

/**
 * Description
 * Ali Oss Test
 * <p>
 * Data
 * 2020/5/16-23:46
 *
 * @author zrx
 * @version 1.0
 */

@SpringBootTest
public class AliOssTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(AliOssTest.class);

//    @Value("${ali.oss.endpoint}")
//    private String endpoint;
//
//
//    @Value("${ali.ram.AccessKey-ID}")
//    private String keyId;
//
//    @Value("${ali.ram.AccessKey-Secret}")
//    private String keySecret;
//
//
//    @Value("${ali.oss.bucket-name}")
//    private String bucketName;
//
//    @Value("${ali.oss.brand-logo-folder}")
//    private String brandLogoFolder;


    @Test
    public void testInject() {
//        LOGGER.info("keyId = {}", keyId);
//        LOGGER.info("keySecret = {}", keySecret);
//        LOGGER.info("endpoint = {}", endpoint);
//        LOGGER.info("bucketName = {}", bucketName);
    }

    @Test
    public void testFileUpload() {
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, keyId, keySecret);
//
//        // 创建PutObjectRequest对象。
//        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, brandLogoFolder+"/aliyun.jpg",
//                new File("C:\\Users\\javalearn\\Documents\\github\\grainmall2020\\grainmall_backend\\docs\\product-brand\\aliyun.jpg"));
//
//
//        // 上传文件。
//        ossClient.putObject(putObjectRequest);
//
//        // 关闭OSSClient。
//        ossClient.shutdown();
//
//        LOGGER.info("上传完成");
    }
}
