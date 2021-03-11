package com.bbnc.voice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bbnc.voice.ThreadLocal.ThreadLocalUser;
import com.bbnc.voice.VO.ResultVO;
import com.bbnc.voice.entity.SysUser;
import com.bbnc.voice.enums.ResultEnum;
import com.bbnc.voice.service.SysUserService;
import com.bbnc.voice.utils.Assert;
import com.bbnc.voice.utils.CommonUtils;
import com.bbnc.voice.utils.ResultVOUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private SysUserService userService;

    @ApiOperation("/管理员审核接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "管理员根据userId审核用户注册", required = true)
    })
    @PutMapping("/examine")
    public ResultVO examine(Integer userId) {
        SysUser user = ThreadLocalUser.getCurrentUser();
        if(CommonUtils.isAdmin(user.getRoleId())) {
            user = userService.getById(userId);
            Assert.assertNotNull(user);
            user.setIsExamine(1);
            userService.updateById(user);
            return ResultVOUtil.success("审核成功");
        }
        return ResultVOUtil.error(ResultEnum.PERMISSION_ERR);
    }

    @ApiOperation("分页获取已通过或未通过审核或全部的用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "index", value = "当前页", required = true),
            @ApiImplicitParam(name = "size", value = "页大小", required = true),
            @ApiImplicitParam(name = "isExamined", value = "该参数控制获取的用户是否已经通过审核（1是通过审核，0是未通过审核），该参数为空的话就返回全部", required = true),
            @ApiImplicitParam(name = "key", value = "该参数可以为空(null),根据用户名搜索关键词")
    })
    @GetMapping("/getUserPage")
    public ResultVO getExaminedUser(Integer index, Integer size, Integer isExamined, String key) {
        Assert.assertNotNull(index, size);
        SysUser user = ThreadLocalUser.getCurrentUser();
        if(CommonUtils.isAdmin(user.getRoleId())) {
            IPage<SysUser> page = new Page<>();
            page.setCurrent(index);
            page.setSize(size);
            QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
            if(isExamined != null) {
                queryWrapper.lambda().eq(SysUser::getIsExamine, isExamined);
            }
            if(key != null) {
                queryWrapper.lambda().like(SysUser::getUserName, key);
            }
            return ResultVOUtil.success(userService.page(page, queryWrapper));
        }
        return ResultVOUtil.error(ResultEnum.PERMISSION_ERR);
    }
}
