package com.bbnc.voice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbnc.voice.dao.UserVideoDao;
import com.bbnc.voice.entity.UserVideo;
import com.bbnc.voice.service.UserVideoService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserVideoServiceImpl extends ServiceImpl<UserVideoDao, UserVideo> implements UserVideoService {
}
