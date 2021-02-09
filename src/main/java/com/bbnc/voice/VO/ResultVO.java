package com.bbnc.voice.VO;

import com.bbnc.voice.enums.ResultEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class ResultVO<T> implements Serializable {

    //错误码
    private Integer code;
    //提示信息
    private String msg;

    private T data;

    public ResultVO() {

    }

    public ResultVO(ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
    }

    public ResultVO(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
