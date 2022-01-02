package com.gwh.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gwh.seckill.pojo.User;
import com.gwh.seckill.vo.LoginVo;
import com.gwh.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author jobob
 * @since 2021-12-08
 */
public interface IUserService extends IService<User> {

    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);

    RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response);
}
