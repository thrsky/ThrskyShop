package com.thrsky.shop.controller.portal;

import com.thrsky.shop.common.Const;
import com.thrsky.shop.common.ServerResponse;
import com.thrsky.shop.pojo.User;
import com.thrsky.shop.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by thRShy on 2017/5/1.
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     *
     *  用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @PostMapping(value = "/login")
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> response=iUserService.login(username,password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;

    }
}
