package com.bbnc.voice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbnc.voice.dao.VideoPathDao;
import com.bbnc.voice.entity.VideoPath;
import com.bbnc.voice.service.VideoPathService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class VideoPathServiceImpl extends ServiceImpl<VideoPathDao, VideoPath> implements VideoPathService {
}
