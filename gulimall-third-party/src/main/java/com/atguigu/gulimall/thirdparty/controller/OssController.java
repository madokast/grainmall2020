package com.atguigu.gulimall.thirdparty.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.atguigu.common.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Description
 * OssController
 * <p>
 * Data
 * 2020/5/17-17:12
 *
 * @author zrx
 * @version 1.0
 */

@RestController
@RequestMapping("/oss")
public class OssController {
    private final static Logger LOGGER = LoggerFactory.getLogger(OssController.class);


    // 这里必须是接口类型才能注入成功
    private final OSS ossClient;


    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint;

    @Value("${spring.cloud.alicloud.oss.bucket-name}")
    private String bucket;

    @Value("${spring.cloud.alicloud.access-key}")
    private String accessId;

    @Value("${server.port}")
    private int serverPort;

    // 动态获取自子的url
    public String getUrl() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(address);
        return "http://" + address.getHostAddress() + ":" + this.serverPort;
    }


    public OssController(OSS ossClient) {
        this.ossClient = ossClient;
    }

    /**
     * {
     *     "accessid": "LTAI4G9d4yHckywVEELQXghw",
     *     "policy": "eyJleHBpcmF0aW9u....",
     *     "signature": "m84uXGONhhlE51cIq4G8qHwha5o=",
     *     "dir": "2020-05-17/",
     *     "host": "https://gulimall-madokast.oss-cn-shenzhen.aliyuncs.com",
     *     "expire": "1589726647"
     * }
     * @return json
     */
    @GetMapping("/policy")
    public Object policy() {
        // 以下 注入/动态获取
//        String accessId = "<yourAccessKeyId>"; // 请填写您的AccessKeyId。
//        String accessKey = "<yourAccessKeySecret>"; // 请填写您的AccessKeySecret。
//        String endpoint = "oss-cn-hangzhou.aliyuncs.com"; // 请填写您的 endpoint。
//        String bucket = "bucket-name"; // 请填写您的 bucketname 。
        String host = "https://" + bucket + "." + endpoint; // host的格式为 bucketname.endpoint
        // callbackUrl为 上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
        String callbackUrl = getUrl();


        // 指定为 当前日期
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String dir = today; // 用户上传文件时指定的前缀。

        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);

        Map<String, String> respMap = new LinkedHashMap<>();

        try {
            long expireTime = 30; // 30秒
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            respMap.put("accessid", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            // respMap.put("expire", formatISO8601Date(expiration));

            // 可惜回调不了 OSS 找不到我
//            JSONObject jasonCallback = new JSONObject();
//            jasonCallback.put("callbackUrl", callbackUrl);
//            jasonCallback.put("callbackBody",
//                    "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}");
//            jasonCallback.put("callbackBodyType", "application/x-www-form-urlencoded");
//            String base64CallbackBody = BinaryUtil.toBase64String(jasonCallback.toString().getBytes());
//            respMap.put("callback", base64CallbackBody);
//
//            JSONObject ja1 = JSONObject.fromObject(respMap);
//            // System.out.println(ja1.toString());
//            response.setHeader("Access-Control-Allow-Origin", "*");
//            response.setHeader("Access-Control-Allow-Methods", "GET, POST");
//            response(request, response, ja1.toString());

        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            ossClient.shutdown();
        }



        return R.ok().put("data",respMap);
    }


}