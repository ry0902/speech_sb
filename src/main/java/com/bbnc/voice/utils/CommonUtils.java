package com.bbnc.voice.utils;

import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.EncoderException;
import ws.schild.jave.InputFormatException;
import ws.schild.jave.MultimediaInfo;
import ws.schild.jave.MultimediaObject;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

public final class CommonUtils {

    public static String fileRootPath = "D:\\myFile2\\Rycode\\speech_recognition\\manage_sb\\videos\\";

    public static String IMAGE_PATH = "D:\\myFile2\\Rycode\\speech_recognition\\manage_sb\\images\\";

    public static final String IMAGE_FORMAT = "jpg";

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
            map.put("name", originalFilename);
            map.put("videoSize", String.valueOf(file.getSize()));
            map.put("videoType", originalFilename.substring(originalFilename.lastIndexOf(".")));
            // 存储路径
            filePath = new StringBuilder(fileRootPath)
                    .append(System.currentTimeMillis())
                    .append(originalFilename)
                    .toString();
            try {
                File f = new File(filePath);
                // 保存文件
                file.transferTo(f);
                map.put("filePath", filePath);
                MultimediaObject instance = new MultimediaObject(f);
                MultimediaInfo result = instance.getInfo();
                Integer ls =  (int)(result.getDuration() / 1000);
                System.out.println("ls = " + ls);
                map.put("videoDuration", ls.toString());
                list.add(map);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InputFormatException e) {
                e.printStackTrace();
            } catch (EncoderException e) {
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

    //图片上传
    public static String writeOut(MultipartFile file, Integer videoId) throws IOException {
        String filename = file.getOriginalFilename();
        Assert.assertNotNull(filename, videoId);

        filename = videoId.toString() + "." + IMAGE_FORMAT;

        File dir = new File(IMAGE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File f = new File(dir, filename);
        InputStream is = new ByteArrayInputStream(file.getBytes());

        BufferedImage image = ImageIO.read(is);

        ImageIO.write(image, IMAGE_FORMAT, new BufferedOutputStream(new FileOutputStream(f)));

        return f.getAbsolutePath();
    }

    //获取本机IP
    public static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("IP地址获取失败" + e.toString());
        }
        return "";
    }

    //判断是否为管理员
    public static boolean isAdmin(Integer userId) {
        return userId == 1;
    }

}
