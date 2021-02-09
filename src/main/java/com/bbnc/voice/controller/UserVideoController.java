package com.bbnc.voice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bbnc.voice.ThreadLocal.ThreadLocalUser;
import com.bbnc.voice.VO.ResultVO;
import com.bbnc.voice.entity.User;
import com.bbnc.voice.entity.UserVideo;
import com.bbnc.voice.service.UserVideoService;
import com.bbnc.voice.utils.Assert;
import com.bbnc.voice.utils.CommonUtils;
import com.bbnc.voice.utils.ResultVOUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userVideo")
public class UserVideoController {

    @Autowired
    private UserVideoService userVideoService;

    @ApiOperation("分页获取当前用户的文件信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "index", value = "当前页", required = true),
            @ApiImplicitParam(name = "size", value = "页大小", required = true),
            @ApiImplicitParam(name = "key", value = "该参数可以为空(null),根据文件名搜索关键词")
    })
    @GetMapping("/getUserVideoPage")
    public ResultVO getUserVideosByPage(Integer index, Integer size, String key) {
        Assert.assertNotNull(index, size);
        User user = ThreadLocalUser.getCurrentUser();
        Assert.assertNotNull(user);
        IPage<UserVideo> page = new Page<>();
        page.setCurrent(index);
        page.setSize(size);
        QueryWrapper<UserVideo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserVideo::getUserId, user.getId());
        if(key != null) {
            queryWrapper.lambda().like(UserVideo::getVideoName, key);
        }
        return ResultVOUtil.success(userVideoService.page(page, queryWrapper));
    }

    @ApiOperation("/修改文件信息接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "记录的id，根据此id确定修改哪条记录", required = true),
            @ApiImplicitParam(name = "remark", value = "文件备注，该参数可以为空(null)"),
            @ApiImplicitParam(name = "videoName", value = "文件名，该参数可以为空(null)")
    })
    @PutMapping("/updateUserVideo")
    public ResultVO updateUserVideo(Integer id, String remark, String videoName) {
        UserVideo userVideo = userVideoService.getById(id);
        Assert.assertNotNull(userVideo);
        if(remark != null) {
            userVideo.setRemark(remark);
        }
        if(videoName != null) {
            userVideo.setVideoName(videoName);
        }
        userVideoService.saveOrUpdate(userVideo);
        return ResultVOUtil.success(userVideo);
    }

    @ApiOperation("根据id删除文件信息接口")
    @DeleteMapping("/deleteUserVideo")
    public ResultVO deleteUserVideo(Integer id) {
        UserVideo userVideo = userVideoService.getById(id);
        Assert.assertNotNull(userVideo);
        CommonUtils.deleteFile(userVideo.getPath());
        userVideoService.removeById(id);
        return ResultVOUtil.success(userVideo);
    }
}
