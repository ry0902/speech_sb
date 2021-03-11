package com.bbnc.voice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bbnc.voice.ThreadLocal.ThreadLocalUser;
import com.bbnc.voice.VO.ResultVO;
import com.bbnc.voice.entity.EduVideoSource;
import com.bbnc.voice.entity.SysUser;
import com.bbnc.voice.entity.VideoPath;
import com.bbnc.voice.enums.ResultEnum;
import com.bbnc.voice.handler.NonStaticResourceHttpRequestHandler;
import com.bbnc.voice.service.EduVideoSourceService;
import com.bbnc.voice.service.VideoPathService;
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
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private EduVideoSourceService eduVideoSourceService;
    @Autowired
    private VideoPathService videoPathService;

    @Autowired
    private NonStaticResourceHttpRequestHandler nonStaticResourceHttpRequestHandler;

    @ApiOperation("视频播放接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId", value = "根据videoId取出视频的绝对路径来播放", required = true)
    })
    @GetMapping("/video")
    public void videoPreview(HttpServletRequest request, HttpServletResponse response, Integer videoId) throws Exception {
        Assert.assertNotNull(videoId);
        QueryWrapper<VideoPath> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VideoPath::getVideoId, videoId);
        //绝对路径
        String realPath = videoPathService.getOne(queryWrapper).getPath();

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

        SysUser user = ThreadLocalUser.getCurrentUser();
        list.forEach(m -> {
            EduVideoSource eduVideoSource =  new EduVideoSource(m.get("name"), m.get("videoSize"), Integer.parseInt(m.get("videoDuration")),1, 1, user.getUserId(), m.get("videoType"));
            eduVideoSourceService.save(eduVideoSource);
            videoPathService.save(new VideoPath(eduVideoSource.getVideoId(), m.get("filePath")));
        });
        return ResultVOUtil.success(map);
    }

    @ApiOperation("视频修改接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "multipartFile", value = "单个文件,上传一个新的视频", required = true),
            @ApiImplicitParam(name = "videoId", value = "记录的videoId，我会根据这个videoId去查询哪个记录", required = true)
    })
    @PostMapping("/update")
    public ResultVO fileUpdate(@RequestParam("file") MultipartFile multipartFile, Integer videoId){
        Assert.assertNotNull(multipartFile, videoId);
        EduVideoSource eduVideoSource = eduVideoSourceService.getById(videoId);
        Assert.assertNotNull(eduVideoSource);
        QueryWrapper<VideoPath> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VideoPath::getVideoId, videoId);
        VideoPath videoPath = videoPathService.getOne(queryWrapper);
        Assert.assertNotNull(videoPath);
        try {
            CommonUtils.deleteFile(videoPath.getPath());
            videoPath.setPath(CommonUtils.uploadFile(multipartFile));
            eduVideoSource.setAddTime(new Timestamp(new Date().getTime()));
            eduVideoSourceService.saveOrUpdate(eduVideoSource);
            videoPathService.update(videoPath, queryWrapper);
            return ResultVOUtil.success("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVOUtil.error(ResultEnum.FILE_UPLOAD_ERR);
        }
    }

    @ApiOperation("下载文件接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId", value = "下载文件的videoId", required = true)
    })
    @PostMapping("/download")
    public void fileDownload(Integer videoId, HttpServletResponse response) {
        Assert.assertNotNull(videoId);
        QueryWrapper<VideoPath> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VideoPath::getVideoId, videoId);
        VideoPath videoPath = videoPathService.getOne(queryWrapper);
        Assert.assertNotNull(videoPath);
        CommonUtils.downloadFile(videoPath.getPath(), response);
    }

    @ApiOperation("上传图片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "用户上传的图片"),
            @ApiImplicitParam(name = "videoId", value = "要上传图片对应文件的videoId")
    })
    @PostMapping("/uploadImg")
    public ResultVO imgUpload(@RequestParam("file") MultipartFile multipartFile, Integer videoId) throws IOException {
        Assert.assertNotNull(videoId);
        EduVideoSource eduVideoSource = eduVideoSourceService.getById(videoId);
        String imgUrl = CommonUtils.writeOut(multipartFile, videoId);
        eduVideoSource.setImageUrl(imgUrl);
        eduVideoSourceService.saveOrUpdate(eduVideoSource);
        return ResultVOUtil.success(imgUrl);
    }

    @ApiOperation("获取图片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId", value = "要上传图片对应文件的videoId")
    })
    @GetMapping("/getImg/{videoId}")
    public void getImage(HttpServletResponse response, @PathVariable Integer videoId) {
        File baseDir  = new File(CommonUtils.IMAGE_PATH);
        File imageFile = new File(baseDir, videoId + "." + CommonUtils.IMAGE_FORMAT);
        if (!imageFile.exists()){
            imageFile = new File(baseDir, "default.png");
        }
        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(imageFile));

            response.setContentType("image/jpeg");
            OutputStream out = response.getOutputStream();

            out.write(inputStream.readAllBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
