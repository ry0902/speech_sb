package com.bbnc.voice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbnc.voice.dao.SysUserDao;
import com.bbnc.voice.entity.SysUser;
import com.bbnc.voice.service.SysUserService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUser> implements SysUserService {
}
