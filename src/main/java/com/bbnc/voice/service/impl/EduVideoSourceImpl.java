package com.bbnc.voice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbnc.voice.dao.EduVideoSourceDao;
import com.bbnc.voice.entity.EduVideoSource;
import com.bbnc.voice.service.EduVideoSourceService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class EduVideoSourceImpl extends ServiceImpl<EduVideoSourceDao, EduVideoSource> implements EduVideoSourceService {
}
