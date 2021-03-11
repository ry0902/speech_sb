package com.bbnc.voice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WavCut {

    @TableId(value = "wav_id", type = IdType.AUTO)
    Integer wavId;

    Integer videoId;

    String wavPath;

    String wavName;

    Float startTime;

    Float endTime;

    String result;

    public WavCut(Integer videoId, String wavPath, String wavName, Float startTime, Float endTime) {
        this.videoId = videoId;
        this.wavPath = wavPath;
        this.wavName = wavName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.result = "";
    }

}
