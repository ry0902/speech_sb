package com.bbnc.voice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUser {

    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    private String loginName;

    @JsonIgnore
    private String loginPwd;

    private String userName;

    private Integer status;

    private Timestamp lastLoginTime;

    private String lastLoginIp;

    private Timestamp createTime;

    private String email;

    private String tel;

    private Integer roleId;

    private Integer bindingRole;

    private String uuid;

    private Integer isExamine;

    public SysUser(String loginName, String loginPwd, String userName, String email, String tel) {
        this.loginName = loginName;
        this.loginPwd = loginPwd;
        this.userName = userName;
        this.email = email;
        this.tel = tel;
        this.status = 0;
        this.createTime = new Timestamp(new Date().getTime());
        this.roleId = 2;
        this.bindingRole = 2;
        this.uuid = UUID.randomUUID().toString().substring(0, 15);
        this.isExamine = 0;
    }

}
