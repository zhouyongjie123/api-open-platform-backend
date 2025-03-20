package com.zyj.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ResponseResult<T> {
    private Integer code;

    private String message;

    private T data;

    public static <T> ResponseResult<T> ok() {
        return new ResponseResult<T>()
                .setCode(ResponseStatus.OK.getCode())
                .setMessage(ResponseStatus.OK.getMessage())
                .setData(null);
    }

    public static <T> ResponseResult<T> ok(T data) {
        return new ResponseResult<T>()
                .setCode(ResponseStatus.OK.getCode())
                .setMessage(ResponseStatus.OK.getMessage())
                .setData(data);
    }
}
