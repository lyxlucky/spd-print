package com.spd.utils;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 文件哈希工具类
 * 用于计算文件的SHA256哈希值
 */
@Component
public class FileHashUtil {
    
    /**
     * 计算文件的SHA256哈希值
     * 
     * @param file 文件对象
     * @return SHA256哈希值（十六进制字符串）
     * @throws IOException 文件读取异常
     */
    public String calculateSHA256(File file) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                
                while ((bytesRead = fis.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
            }
            
            byte[] hashBytes = digest.digest();
            return bytesToHex(hashBytes);
            
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("SHA-256算法不可用", e);
        }
    }
    
    /**
     * 计算文件的SHA1哈希值
     * 
     * @param file 文件对象
     * @return SHA1哈希值（十六进制字符串）
     * @throws IOException 文件读取异常
     */
    public String calculateSHA1(File file) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                
                while ((bytesRead = fis.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
            }
            
            byte[] hashBytes = digest.digest();
            return bytesToHex(hashBytes);
            
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("SHA-1算法不可用", e);
        }
    }
    
    /**
     * 将字节数组转换为十六进制字符串
     * 
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
