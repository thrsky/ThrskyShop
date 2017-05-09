package com.thrsky.shop.controller.portal;

import com.thrsky.shop.common.Const;
import com.thrsky.shop.common.LoginEnum;
import com.thrsky.shop.common.ServerResponse;
import com.thrsky.shop.pojo.User;
import com.thrsky.shop.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping(value = "/logout")
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    @GetMapping(value = "/register")
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    @GetMapping(value = "/checkValid")
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    @GetMapping(value = "/getUserInfo")
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if(user!=null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByError(LoginEnum.USER_NO_LOGIN.getMsg());
    }

    @GetMapping(value = "/forgetPasswdGetQuestion")
    @ResponseBody
    public ServerResponse<String> forgetPasswdGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    @GetMapping(value = "/forgetPasswdCheckAnswer")
    @ResponseBody
    public ServerResponse<String> forgetPasswdCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,answer,question);
    }
}
