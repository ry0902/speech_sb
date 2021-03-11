package com.bbnc.voice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bbnc.voice.ThreadLocal.ThreadLocalUser;
import com.bbnc.voice.VO.ResultVO;
import com.bbnc.voice.entity.EduVideoSource;
import com.bbnc.voice.entity.SysUser;
import com.bbnc.voice.entity.VideoPath;
import com.bbnc.voice.service.EduVideoSourceService;
import com.bbnc.voice.service.VideoPathService;
import com.bbnc.voice.utils.Assert;
import com.bbnc.voice.utils.CommonUtils;
import com.bbnc.voice.utils.ResultVOUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/eduVideoSource")
public class EduVideoSourceController {

    @Autowired
    private EduVideoSourceService eduVideoSourceService;

    @Autowired
    private VideoPathService videoPathService;

    @ApiOperation("分页获取当前用户的文件信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "index", value = "当前页", required = true),
            @ApiImplicitParam(name = "size", value = "页大小", required = true),
            @ApiImplicitParam(name = "key", value = "该参数可以为空(null),根据文件名搜索关键词")
    })
    @GetMapping("/getEduVideoPage")
    public ResultVO getEduVideoPage(Integer index, Integer size, String key) {
        Assert.assertNotNull(index, size);
        SysUser user = ThreadLocalUser.getCurrentUser();
        Assert.assertNotNull(user);
        IPage<EduVideoSource> page = new Page<>();
        page.setCurrent(index);
        page.setSize(size);
        QueryWrapper<EduVideoSource> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EduVideoSource::getUploadUserId, user.getUserId());
        if(key != null) {
            queryWrapper.lambda().like(EduVideoSource::getName, key);
        }
        return ResultVOUtil.success(eduVideoSourceService.page(page, queryWrapper));
    }

    @ApiOperation("/修改文件信息接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId", value = "记录的videoId，根据此videoId确定修改哪条记录", required = true),
            @ApiImplicitParam(name = "videoName", value = "文件名，该参数可以为空(null)")
    })
    @PutMapping("/updateEduVideo")
    public ResultVO updateEduVideo(Integer videoId, String videoName) {
        EduVideoSource eduVideoSource = eduVideoSourceService.getById(videoId);
        Assert.assertNotNull(eduVideoSource);
        if(videoName != null) {
            eduVideoSource.setName(videoName);
        }
        eduVideoSourceService.saveOrUpdate(eduVideoSource);
        return ResultVOUtil.success(eduVideoSource);
    }

    @ApiOperation("根据videoId删除文件信息接口")
    @DeleteMapping("/deleteEduVideo")
    public ResultVO deleteEduVideo(Integer videoId) {
        EduVideoSource eduVideoSource = eduVideoSourceService.getById(videoId);
        Assert.assertNotNull(eduVideoSource);
        eduVideoSourceService.removeById(videoId);
        QueryWrapper<VideoPath> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VideoPath::getVideoId, videoId);
        VideoPath videoPath = videoPathService.getOne(queryWrapper);
        Assert.assertNotNull(videoPath);
        CommonUtils.deleteFile(videoPath.getPath());
        videoPathService.remove(queryWrapper);
        return ResultVOUtil.success("删除成功");
    }

    @ApiOperation("根据videoId获取视频信息")
    @GetMapping("/getEduVideoById")
    public ResultVO getEduVideoById(Integer videoId) {
        Assert.assertNotNull(videoId);
        return ResultVOUtil.success(eduVideoSourceService.getById(videoId));
    }
}
