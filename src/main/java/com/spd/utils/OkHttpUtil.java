package com.spd.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp工具类
 * 封装了常用的HTTP请求方法
 */
@Component
public class OkHttpUtil {
    
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");
    
    private final OkHttpClient client;
    
    public OkHttpUtil() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .callTimeout(45, TimeUnit.SECONDS)
                .build();
    }
    
    /**
     * 发送GET请求
     * 
     * @param url 请求URL
     * @return 响应字符串
     * @throws IOException IO异常
     */
    public String get(String url) throws IOException {
        return get(url, null);
    }
    
    /**
     * 发送GET请求（带请求头）
     * 
     * @param url 请求URL
     * @param headers 请求头
     * @return 响应字符串
     * @throws IOException IO异常
     */
    public String get(String url, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder().url(url);
        
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        
        Request request = builder.build();
        
        return executeRequest(request);
    }
    
    /**
     * 发送POST请求（JSON格式数据）
     * 
     * @param url 请求URL
     * @param json JSON数据
     * @return 响应字符串
     * @throws IOException IO异常
     */
    public String postJson(String url, String json) throws IOException {
        return postJson(url, json, null);
    }
    
    /**
     * 发送POST请求（JSON格式数据）
     * 
     * @param url 请求URL
     * @param jsonMap JSON数据Map
     * @return 响应字符串
     * @throws IOException IO异常
     */
    public String postJson(String url, Map<String, String> jsonMap) throws IOException {
        return postJson(url, jsonMap, null);
    }
    
    /**
     * 发送POST请求（JSON格式数据，带请求头）
     *
     * @param url 请求URL
     * @param jsonMap JSON数据Map
     * @param headers 请求头
     * @return 响应字符串
     * @throws IOException IO异常
     */
    public String postJson(String url, Map<String, String> jsonMap, Map<String, String> headers) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(jsonMap);
        return postJson(url, json, headers);
    }
    
    /**
     * 发送POST请求（JSON格式数据，带请求头）
     *
     * @param url 请求URL
     * @param json JSON数据
     * @param headers 请求头
     * @return 响应字符串
     * @throws IOException IO异常
     */
    public String postJson(String url, String json, Map<String, String> headers) throws IOException {
        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);
        
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        
        Request request = builder.build();
        return executeRequest(request);
    }
    
    /**
     * 发送POST请求（表单数据）
     * 
     * @param url 请求URL
     * @param formData 表单数据
     * @return 响应字符串
     * @throws IOException IO异常
     */
    public String postForm(String url, Map<String, String> formData) throws IOException {
        return postForm(url, formData, null);
    }
    
    /**
     * 发送POST请求（表单数据，带请求头）
     *
     * @param url 请求URL
     * @param formData 表单数据
     * @param headers 请求头
     * @return 响应字符串
     * @throws IOException IO异常
     */
    public String postForm(String url, Map<String, String> formData, Map<String, String> headers) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (formData != null) {
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                formBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        
        RequestBody formBody = formBuilder.build();
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(formBody);
        
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        
        Request request = builder.build();
        return executeRequest(request);
    }
    
    /**
     * 发送POST请求（multipart/form-data类型表单）
     * 
     * @param url 请求URL
     * @param formData 表单数据
     * @return 响应字符串
     * @throws IOException IO异常
     */
    public String postMultipart(String url, Map<String, String> formData) throws IOException {
        return postMultipartFormData(url, formData, null, null);
    }
    

    /**
     * 发送POST请求（multipart/form-data类型表单，支持文件上传和请求头）
     * 
     * @param url 请求URL
     * @param formData 表单数据
     * @param fileData 文件数据，key为字段名，value为文件路径或URL
     * @param headers 请求头
     * @return 响应字符串
     * @throws IOException IO异常
     */
    public String postMultipartFormData(String url, Map<String, String> formData, Map<String, File> fileData, Map<String, String> headers) throws IOException {
        // 生成自定义boundary，避免自动添加charset
        String boundary = "----Boundary" + System.currentTimeMillis() + Math.abs(new java.util.Random().nextLong());

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder(boundary)
                .setType(MultipartBody.FORM);

        // 添加文本字段
        if (formData != null) {
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                multipartBuilder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }

        // 添加文件字段
        if (fileData != null) {
            for (Map.Entry<String, File> entry : fileData.entrySet()) {
                File file = entry.getValue();
                if (!file.exists()) {
                    throw new IOException("文件不存在: " + file.getAbsolutePath());
                }

                Path path = file.toPath();
                String contentType = Files.probeContentType(path);
                MediaType mediaType = contentType != null ? MediaType.get(contentType) : MediaType.parse("application/octet-stream");

                multipartBuilder.addFormDataPart(
                        entry.getKey(),
                        file.getName(),
                        RequestBody.create(file, mediaType)
                );
            }
        }

        RequestBody requestBody = multipartBuilder.build();

        // 👇 手动构建正确的 Content-Type，包含我们生成的 boundary，但不带 charset
        String contentTypeHeader = "multipart/form-data; boundary=" + boundary;

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Content-Type", contentTypeHeader);  // ✅ 使用我们控制的 Content-Type

        // 添加其他请求头
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBuilder.addHeader(header.getKey(), header.getValue());
            }
        }

        Request request = requestBuilder.build();
        return executeRequest(request);
    }
    /**
     * 执行HTTP请求并处理响应
     * 
     * @param request 请求对象
     * @return 响应字符串
     * @throws IOException IO异常
     */
    private String executeRequest(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("请求失败，状态码: " + response.code() + ", URL: " + request.url());
            }
            
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new IOException("响应体为空, URL: " + request.url());
            }
            
            return responseBody.string();
        }
    }
    
    /**
     * 获取OkHttpClient实例
     * 
     * @return OkHttpClient实例
     */
    public OkHttpClient getClient() {
        return client;
    }
}