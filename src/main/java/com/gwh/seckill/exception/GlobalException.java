package com.gwh.seckill.exception;

import com.gwh.seckill.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 全局异常
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GlobalException extends RuntimeException{
    private RespBeanEnum respBeanEnum;
}
