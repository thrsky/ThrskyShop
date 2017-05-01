package com.thrsky.shop.service.impl;

import com.thrsky.shop.common.LoginEnum;
import com.thrsky.shop.common.ServerResponse;
import com.thrsky.shop.dao.UserMapper;
import com.thrsky.shop.pojo.User;
import com.thrsky.shop.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by thRShy on 2017/5/1.
 */
@Service(value = "iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resCount=userMapper.checkUserName(username);
        if(resCount<1){
            return ServerResponse.createByError(LoginEnum.NO_USER.getMsg());
        }

        //TODO 密码登录MD5

        User user=userMapper.userLogin(username,password);
        if(user!=null){
            return ServerResponse.createByError(LoginEnum.ERROR_PASSWORD.getMsg());
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(LoginEnum.LOGIN_SUCCESS.getMsg(),user);
    }
}
