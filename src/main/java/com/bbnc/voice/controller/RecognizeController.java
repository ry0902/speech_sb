package com.bbnc.voice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bbnc.voice.VO.ResultVO;
import com.bbnc.voice.entity.EduVideoSource;
import com.bbnc.voice.entity.WavCut;
import com.bbnc.voice.recognize.ApiUtils;
import com.bbnc.voice.recognize.wav;
import com.bbnc.voice.service.EduVideoSourceService;
import com.bbnc.voice.service.WavCutService;
import com.bbnc.voice.utils.Assert;
import com.bbnc.voice.utils.ResultVOUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class RecognizeController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EduVideoSourceService eduVideoSourceService;

    @Autowired
    private WavCutService wavCutService;

    @ApiOperation("单条wav语音识别")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoName", value = "视频名,如kanghui1min,不带.mp4"),
            @ApiImplicitParam(name = "wavName", value = "wav名字，带.wav")
    })
    @PostMapping("/recognizeWav")
    public ResultVO recognize(String videoName, String wavName) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream("D:\\speech\\output\\" + videoName + "\\" + wavName));
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        byte[] temp = new byte[1024];
        int size = 0;
        while ((size = in.read(temp)) != -1) {
            out.write(temp, 0, size);
        }
        in.close();
        byte[] content = out.toByteArray();
        wav wavefile = new wav();
        wavefile.GetFromBytes(content);
        String result = ApiUtils.SendToASRTServer("qwertasd", wavefile.fs, wavefile.samples);
//        System.out.println("result = " + result);
        return ResultVOUtil.success(result);
    }

    @ApiOperation("语音识别接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId", value = "需要解析视频的id", required = true)
    })
    @PostMapping("/recognizeVideo")
    public ResultVO recognize(Integer videoId) throws InterruptedException {
        Assert.assertNotNull(videoId);
        EduVideoSource eduVideoSource = eduVideoSourceService.getById(videoId);
        Assert.assertNotNull(eduVideoSource);

        String url = "http://10.30.90.18:8080/getWav/" + eduVideoSource.getName();

        ResponseEntity<List> responseEntity = restTemplate.getForEntity(
                url,
                List.class
        );

        List<Map<String, Object>> list = responseEntity.getBody();
        List<WavCut> resultList = new ArrayList<>();
        list.forEach(map -> {
            resultList.add(new WavCut(videoId, (String)map.get("wavPath"), (String)map.get("wavName"), Float.parseFloat(map.get("startTime")+""), Float.parseFloat(map.get("endTime")+"")));
        });

        resultList.forEach(wavCut -> {
            BufferedInputStream in = null;
            try {
                in = new BufferedInputStream(new FileInputStream(wavCut.getWavPath()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            byte[] temp = new byte[1024];
            int size = 0;
            while (true) {
                try {
                    if (!((size = in.read(temp)) != -1)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out.write(temp, 0, size);
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] content = out.toByteArray();
            wav wavefile = new wav();
            wavefile.GetFromBytes(content);
            String result = ApiUtils.SendToASRTServer("qwertasd", wavefile.fs, wavefile.samples);
            System.out.println("result = " + result);
            wavCut.setResult(result);
            QueryWrapper<WavCut> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(WavCut::getWavPath, wavCut.getWavPath());
            WavCut wc = wavCutService.getOne(queryWrapper);
            if(wc == null) {
                wavCutService.save(wavCut);
            }
            else {
                wavCut.setWavId(wc.getWavId());
                wavCutService.saveOrUpdate(wavCut);
            }
        });
        return ResultVOUtil.success(resultList);
    }

}
