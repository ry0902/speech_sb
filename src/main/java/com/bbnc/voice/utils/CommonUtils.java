package com.bbnc.voice.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CommonUtils {

    private static String fileRootPath = "D:\\myFile2\\Rycode\\speech_recognition\\manage_sb\\videos\\";

    //多文件上传
    public static List<Map<String, String>> uploadFiles(MultipartFile[] files){
        List<Map<String, String>> list = new ArrayList<>();
        File dir = new File(fileRootPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filePath = "";
        for (MultipartFile file : files){
            // 上传简单文件名
            Map<String, String> map = new HashMap<>();
            String originalFilename = file.getOriginalFilename();
            map.put("originalFilename", originalFilename);
            // 存储路径
            filePath = new StringBuilder(fileRootPath)
                    .append(System.currentTimeMillis())
                    .append(originalFilename)
                    .toString();
            try {
                // 保存文件
                file.transferTo(new File(filePath));
                map.put("filePath", filePath);
                list.add(map);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    //单文件上传
    public static String uploadFile(MultipartFile file) {
        File dir = new File(fileRootPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filePath = "";
        String originalFilename = file.getOriginalFilename();
        // 存储路径
        filePath = new StringBuilder(fileRootPath)
                .append(System.currentTimeMillis())
                .append(originalFilename)
                .toString();
        try {
            // 保存文件
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    //文件删除
    public static void deleteFile(String path) {
        System.out.println("path = " + path);
        File file = new File(path);
        if(file.exists()) {
            file.delete();
        }
    }

    //文件下载
    public static boolean downloadFile(String path, HttpServletResponse response) {
        File file = new File(path);
        if(!file.exists()){
            return false;
        }
        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        System.out.println("fileName = " + file.getName());
        response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());

        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buff = new byte[1024];
            OutputStream os  = response.getOutputStream();
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
            System.out.println("文件流输出成功");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
