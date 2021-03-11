package com.bbnc.voice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@TableName(value = "edu_videosource")
public class EduVideoSource {

    @TableId(value = "videoId", type = IdType.AUTO)
    private Integer videoId;

    @TableField(value = "name")
    private String name;

    @TableField(value = "videoSize")
    private String videoSize;

    @TableField(value = "addTime")
    private Timestamp addTime;

    @TableField(value = "status")
    private String status;

    @TableField(value = "videoLength")
    private String videoLength;

    @TableField(value = "idVarchar")
    private String idVarchar;

    @TableField(value = "videoDuration")
    private Integer videoDuration;

    @TableField(value = "imageUrl")
    private String imageUrl;

    @TableField(value = "fileType")
    private Integer fileType;

    @TableField(value = "initType")
    private Integer initType;

    @TableField(value = "uploadUserId")
    private Integer uploadUserId;

    @TableField(value = "videoType")
    private String videoType;

    public EduVideoSource(String name, String videoSize, Integer videoDuration, Integer fileType, Integer initType, Integer uploadUserId, String videoType) {
        this.name = name;
        this.videoSize = videoSize;
        this.videoDuration = videoDuration;
        this.addTime = new Timestamp(new Date().getTime());
        this.status = "init";
        this.idVarchar = UUID.randomUUID().toString().substring(0, 20);
        this.fileType = fileType;
        this.initType = initType;
        this.uploadUserId = uploadUserId;
        this.videoType = videoType;
    }
}
