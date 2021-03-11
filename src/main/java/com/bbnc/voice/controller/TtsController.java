package com.bbnc.voice.controller;

import com.bbnc.voice.VO.ResultVO;
import com.bbnc.voice.entity.EduVideoSource;
import com.bbnc.voice.enums.ResultEnum;
import com.bbnc.voice.service.EduVideoSourceService;
import com.bbnc.voice.tts.TtsTool;
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

@RestController
public class TtsController {

    @Autowired
    private EduVideoSourceService eduVideoSourceService;

    @Autowired
    private RestTemplate restTemplate;

    @ApiOperation("语音合成接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId", value = "视频videoId"),
            @ApiImplicitParam(name = "wavName", value = "wav名，001.wav"),
            @ApiImplicitParam(name = "text", value = "文本"),
    })
    @PostMapping("/textToVoice")
    public ResultVO textToVoice(Integer videoId, String wavName, String text) {
        Assert.assertNotNull(videoId, wavName);
        EduVideoSource eduVideoSource = eduVideoSourceService.getById(videoId);

        String fileName = eduVideoSourceService.getById(videoId).getName();
        String wavePath = "D:\\speech\\output\\" + fileName.substring(0, fileName.lastIndexOf(".")) + "\\";
        TtsTool ttsTool = new TtsTool("603b1f25", wavePath, text.length());
        ttsTool.textToVoice(wavName.substring(0, wavName.lastIndexOf(".")), text);

        String url = "http://10.30.90.18:8080/audioConversion/" + eduVideoSource.getName() + "/" + wavName;
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                url,
                String.class
        );

        String responseCode = responseEntity.getBody();
        if(responseCode.equals("200")){
            return ResultVOUtil.success("成功");
        }
        return ResultVOUtil.error(ResultEnum.FAIL);
    }

    @ApiOperation("合并wav")
    @ApiImplicitParam(name = "videoName", value = "视频名称")
    @PostMapping("/mergeWav")
    public ResultVO mergeWav(String videoName) {
        Assert.assertNotNull(videoName);
        String url = "http://10.30.90.18:8080/mergeWav/" + videoName;
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                url,
                String.class
        );

        String responseCode = responseEntity.getBody();
        if(responseCode.equals("200")){
            return ResultVOUtil.success("成功");
        }
        return ResultVOUtil.error(ResultEnum.FAIL);
    }

    @ApiOperation("wav替换到视频")
    @ApiImplicitParam(name = "videoName", value = "视频名称")
    @PostMapping("/mergeVideo")
    public ResultVO mergeVideo(String videoName) {
        Assert.assertNotNull(videoName);
        String url = "http://10.30.90.18:8080/getVideo/" + videoName;
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                url,
                String.class
        );

        String responseCode = responseEntity.getBody();
        if(responseCode.equals("200")){
            return ResultVOUtil.success("成功");
        }
        return ResultVOUtil.error(ResultEnum.FAIL);
    }
}
