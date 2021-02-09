package com.bbnc.voice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbnc.voice.dao.UserDao;
import com.bbnc.voice.entity.User;
import com.bbnc.voice.service.UserService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {
}
