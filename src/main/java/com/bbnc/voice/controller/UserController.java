package com.bbnc.voice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bbnc.voice.VO.ResultVO;
import com.bbnc.voice.entity.User;
import com.bbnc.voice.enums.ResultEnum;
import com.bbnc.voice.jwt.util.TokenUtil;
import com.bbnc.voice.service.UserService;
import com.bbnc.voice.utils.Assert;
import com.bbnc.voice.utils.ResultVOUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true),
            @ApiImplicitParam(name = "password", value = "密码", required = true),
    })
    @PostMapping("/login")
    public ResultVO login(String username, String password) {
        Assert.assertNotNull(username, password);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUsername, username);
        User user = userService.getOne(queryWrapper);
        if(user != null && user.getPassword().equals(password)){
            String token = TokenUtil.sign(user);
            HashMap<String, Object> hs = new HashMap<>();
            hs.put("token", token);
            return ResultVOUtil.success(hs);
        }
        return ResultVOUtil.error(ResultEnum.LOGIN_FAIL);
    }

    @ApiOperation("根据用户id获取用户信息")
    @GetMapping("/getUser")
    public ResultVO getUser(Integer userId){
        return ResultVOUtil.success(userService.getById(userId));
    }
}
