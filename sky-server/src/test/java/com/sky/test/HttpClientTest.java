package com.sky.test;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HttpClientTest {

    @Test
    public void testGet() throws Exception {
        // 1. 先创建客户端
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 2. 连接
        HttpGet httpGet = new HttpGet("http://localhost:8080/admin/shop/status");
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

        // 3. 获取response
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        System.out.println("statusCode = " + statusCode );

        HttpEntity responseEntity = httpResponse.getEntity();
        // 这个entity是不能直接被print出来的,需要用一个entity的utils转换一下
        String entityString = EntityUtils.toString(responseEntity);
        System.out.println("entity = " + entityString);

        // 4. 关闭资源
        httpResponse.close();
        httpClient.close();
    }

    @Test
    public void testPost() throws Exception {
        // 测试一个登录接口
        CloseableHttpClient httpClient = HttpClients.createDefault();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "admin");
        jsonObject.put("password", "123456");

        StringEntity httpEntity = new StringEntity(jsonObject.toString());
        httpEntity.setContentEncoding("utf-8");
        httpEntity.setContentType("application/json");

        HttpPost httpPost = new HttpPost("http://localhost:8080/admin/employee/login");
        httpPost.setEntity(httpEntity);

        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        System.out.println(httpResponse.getStatusLine().getStatusCode());
        System.out.println(EntityUtils.toString(httpResponse.getEntity()));

        httpResponse.close();
        httpClient.close();
    }


}
