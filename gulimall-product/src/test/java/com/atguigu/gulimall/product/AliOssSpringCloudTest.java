package com.atguigu.gulimall.product;

//import com.aliyun.oss.OSSClient;
//import com.aliyun.oss.model.PutObjectRequest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;

/**
 * Description
 * AliOssSpringCloudTest
 * <p>
 * Data
 * 2020/5/17-9:32
 *
 * @author zrx
 * @version 1.0
 */

// 学习一下，springboot 2.2 后，不再用 @RunWith 注解，这好像是 junit4 的东西，现在升到 junit5 了
// POM 包里面也排除了 junit-vintage-engine
@SpringBootTest
public class AliOssSpringCloudTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(AliOssSpringCloudTest.class);

    //#spring:
    //#  cloud:
    //#    alicloud:
    //#      access-key: XXX
    //#      secret-key: XXX
    //#      oss:
    //#        endpoint: XXX
//
//    @Autowired
//    private OSSClient ossClient;
//
//    @Value("${spring.cloud.alicloud.oss.bucket-name}")
//    private String bucketName;
//
//    @Value("${spring.cloud.alicloud.oss.brand-logo-folder}")
//    public String brandLogoFolder;

    @Test
    public void testFileUpload() {

//        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, brandLogoFolder + "/elementUI.jpg",
//                new File("C:\\Users\\javalearn\\Documents\\github\\grainmall2020\\grainmall_backend\\docs\\product-brand\\elementUI.jpg"));
//
//        ossClient.putObject(putObjectRequest);
//
//        ossClient.shutdown();
//
//        LOGGER.info("上传成功");
    }

}
