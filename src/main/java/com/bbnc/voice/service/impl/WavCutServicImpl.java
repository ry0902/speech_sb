package com.bbnc.voice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbnc.voice.dao.WavCutDao;
import com.bbnc.voice.entity.WavCut;
import com.bbnc.voice.service.WavCutService;
import org.springframework.stereotype.Service;

@Service
public class WavCutServicImpl extends ServiceImpl<WavCutDao, WavCut> implements WavCutService {
}
