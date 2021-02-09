package com.bbnc.voice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class UserVideo {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private String path;

    private Date uploadTime;

    private String remark;

    private String videoName;

    public UserVideo(Integer userId, String path, String videoName) {
        this.userId = userId;
        this.path = path;
        this.remark = "暂无视频说明!";
        this.videoName = videoName;
        this.uploadTime = new Date();
    }
}
