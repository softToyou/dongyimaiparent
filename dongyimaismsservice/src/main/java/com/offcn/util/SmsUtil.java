package com.offcn.util;

import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.Map;

@Component
public class SmsUtil {

    @Value("${AppCode}")
    private String appcode;

    @Value("${tpl}")
    private String tpl;

    private String host = "http://dingxin.market.alicloudapi.com";

    //发送短信

    public HttpResponse sendCode(String mobile, String code) throws Exception{
        String host = "http://dingxin.market.alicloudapi.com";
        String path = "/dx/sendSms";
        String method = "POST";


        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", mobile);
        querys.put("param", "code:" + code);
        querys.put("tpl_id", tpl);
        Map<String, String> bodys = new HashMap<String, String>();



        HttpResponse response = (HttpResponse) HttpUtils.doPost(host, path, method, headers, querys, bodys);

//        System.out.println(response.toString());
        //获取response的body
        //System.out.println(EntityUtils.toString(response.getEntity()));

        return response;
    }
}
