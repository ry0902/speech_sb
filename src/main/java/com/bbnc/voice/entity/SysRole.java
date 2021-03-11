package com.bbnc.voice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class SysRole {

    @TableId(value = "roleId", type = IdType.AUTO)
    private Integer roleId;

    private String roleName;

}
