package com.bbnc.voice.controller;

import com.bbnc.voice.ThreadLocal.ThreadLocalUser;
import com.bbnc.voice.VO.ResultVO;
import com.bbnc.voice.entity.User;
import com.bbnc.voice.entity.UserVideo;
import com.bbnc.voice.enums.ResultEnum;
import com.bbnc.voice.handler.NonStaticResourceHttpRequestHandler;
import com.bbnc.voice.service.UserVideoService;
import com.bbnc.voice.utils.Assert;
import com.bbnc.voice.utils.CommonUtils;
import com.bbnc.voice.utils.ResultVOUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private UserVideoService userVideoService;

    @Autowired
    private NonStaticResourceHttpRequestHandler nonStaticResourceHttpRequestHandler;

    @ApiOperation("视频播放接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "根据id取出视频的绝对路径来播放", required = true)
    })
    @GetMapping("/video")
    public void videoPreview(HttpServletRequest request, HttpServletResponse response, Integer id) throws Exception {
        Assert.assertNotNull(id);
        //绝对路径
        String realPath = userVideoService.getById(id).getPath();

        Path filePath = Paths.get(realPath);
        if (Files.exists(filePath)) {
            String mimeType = Files.probeContentType(filePath);
            if (!StringUtils.isEmpty(mimeType)) {
                response.setContentType(mimeType);
            }
            request.setAttribute(NonStaticResourceHttpRequestHandler.ATTR_FILE, filePath);
            nonStaticResourceHttpRequestHandler.handleRequest(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        }
    }


    @ApiOperation("视频上传接口")
    @PostMapping("/upload")
    public ResultVO fileUpload(@RequestParam("files") MultipartFile[] files){
        // 多文件上传
        List<Map<String, String>> list = CommonUtils.uploadFiles(files);
        Integer failCount = files.length - list.size();
        Map<String, Object> map = new HashMap<>();
        map.put("failCount", failCount);
        map.put("successList", list);

        User user = ThreadLocalUser.getCurrentUser();
        list.forEach(m -> {
            userVideoService.save(new UserVideo(user.getId(), m.get("filePath"), m.get("originalFilename")));
        });
        return ResultVOUtil.success(map);
    }

    @ApiOperation("视频修改接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "multipartFile", value = "单个文件,上传一个新的视频", required = true),
            @ApiImplicitParam(name = "id", value = "记录的id，我会根据这个id去查询哪个记录", required = true)
    })
    @PutMapping("/update")
    public ResultVO fileUpdate(@RequestParam("file") MultipartFile multipartFile, Integer id){
        Assert.assertNotNull(multipartFile, id);
        UserVideo userVideo = userVideoService.getById(id);
        Assert.assertNotNull(userVideo);
        try {
            CommonUtils.deleteFile(userVideo.getPath());
            userVideo.setPath(CommonUtils.uploadFile(multipartFile));
            userVideo.setUploadTime(new Date());
            userVideoService.saveOrUpdate(userVideo);
            return ResultVOUtil.success(userVideo);
        } catch (Exception e) {
            return ResultVOUtil.error(ResultEnum.FILE_UPLOAD_ERR);
        }
    }

    @ApiOperation("下载文件接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path", value = "下载文件的路径", required = true)
    })
    @PostMapping("/download")
    public void fileDownload(Integer id, HttpServletResponse response) {
        UserVideo userVideo = userVideoService.getById(id);
        Assert.assertNotNull(userVideo);
        CommonUtils.downloadFile(userVideo.getPath(), response);
    }
}
