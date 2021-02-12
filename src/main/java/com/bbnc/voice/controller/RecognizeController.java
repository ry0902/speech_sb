package com.bbnc.voice.controller;

import com.bbnc.voice.recognize.ApiUtils;
import com.bbnc.voice.recognize.wav;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.io.*;

@RestController
public class RecognizeController {

    @PostMapping("/recognize")
    public void recognize(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream("D:\\myFile2\\Rycode\\speech_recognition\\resource\\lfasr.wav"));
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        byte[] temp = new byte[1024];
        int size = 0;
        while ((size = in.read(temp)) != -1) {
            out.write(temp, 0, size);
        }
        in.close();
        byte[] content = out.toByteArray();
//        String tobase64 = Base64.getEncoder().encodeToString(content);
//        byte[] bytes_wav = ApiUtils.Convert_Base64ToBytes(tobase64);
        wav wavefile = new wav();
        wavefile.GetFromBytes(content);
        String result = ApiUtils.SendToASRTServer("qwertasd", wavefile.fs, wavefile.samples);
        System.out.println("result = " + result);
    }

}
