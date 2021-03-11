package com.bbnc.voice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bbnc.voice.ThreadLocal.ThreadLocalUser;
import com.bbnc.voice.VO.ResultVO;
import com.bbnc.voice.entity.SysUser;
import com.bbnc.voice.entity.dto.UserDto;
import com.bbnc.voice.enums.ResultEnum;
import com.bbnc.voice.jwt.util.TokenUtil;
import com.bbnc.voice.service.SysUserService;
import com.bbnc.voice.utils.Assert;
import com.bbnc.voice.utils.CommonUtils;
import com.bbnc.voice.utils.ResultVOUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    @ApiOperation("注册接口")
    @PostMapping("/register")
    public ResultVO register(@RequestBody UserDto userDto) {
        SysUser user = new SysUser(userDto.getLoginName(), userDto.getLoginPwd(), userDto.getUserName(), userDto.getEmail(), userDto.getTel());
        sysUserService.save(user);
        return ResultVOUtil.success(user);
    }

    @ApiOperation("登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginName", value = "用户名", required = true),
            @ApiImplicitParam(name = "loginPwd", value = "密码", required = true),
    })
    @PostMapping("/login")
    public ResultVO login(String loginName, String loginPwd) {
        Assert.assertNotNull(loginName, loginPwd);
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUser::getLoginName, loginName);
        SysUser user = sysUserService.getOne(queryWrapper);
        if(user != null && user.getLoginPwd().equals(loginPwd) && user.getIsExamine() == 1 && user.getStatus() == 0){
            user.setLastLoginTime(new Timestamp(new Date().getTime()));
            user.setLastLoginIp(CommonUtils.getIpAddress());
            sysUserService.saveOrUpdate(user);
            String token = TokenUtil.sign(user);
            HashMap<String, Object> hs = new HashMap<>();
            hs.put("token", token);
            return ResultVOUtil.success(hs);
        }
        return ResultVOUtil.error(ResultEnum.LOGIN_FAIL);
    }

    @GetMapping("/getUser")
    public ResultVO getUser(Integer userId){
        SysUser user = ThreadLocalUser.getCurrentUser();
        if(CommonUtils.isAdmin(user.getRoleId())) {
            return ResultVOUtil.success(sysUserService.getById(userId));
        }
        else if(userId.equals(user.getUserId())) {
            return ResultVOUtil.success(sysUserService.getById(userId));
        }
        return ResultVOUtil.error(ResultEnum.PERMISSION_ERR);
    }
}
