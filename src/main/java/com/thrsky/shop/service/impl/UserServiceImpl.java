package com.thrsky.shop.service.impl;

import com.thrsky.shop.common.*;
import com.thrsky.shop.dao.UserMapper;
import com.thrsky.shop.pojo.User;
import com.thrsky.shop.service.IUserService;
import com.thrsky.shop.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
        String md5Password=MD5Util.MD5EncodeUtf8(password);
        User user=userMapper.userLogin(username,md5Password);
        if(user!=null){
            return ServerResponse.createByError(LoginEnum.ERROR_PASSWORD.getMsg());
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(LoginEnum.LOGIN_SUCCESS.getMsg(),user);
    }

    @Override
    public ServerResponse<String> register(User user){
//        //先验证用户名是否被占用
//        int resCount=userMapper.checkUserName(user.getUsername());
//        if(resCount>0){
//            return ServerResponse.createByError(RegisterEnum.USERNAME_EXIST.getMsg());
//        }
//        //接下来我们判断 邮箱
//        resCount=userMapper.checkEmail(user.getEmail());
//        if(resCount>0){
//            return ServerResponse.createByError(RegisterEnum.EMAIL_EXIST.getMsg());
//        }

        ServerResponse validReponse=this.checkValid(user.getUsername(),Const.USERNAME);
        if(!validReponse.isSuccess())
            return validReponse;
        validReponse=this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validReponse.isSuccess())
            return validReponse;
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //md5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resCount=userMapper.insert(user);
        if(resCount==0){
            return ServerResponse.createByError(RegisterEnum.REGISTER_ERROR.getMsg());
        }
        return ServerResponse.createBySuccessMessage(RegisterEnum.REGISTER_SUCESS.getMsg());
    }

    public ServerResponse<String> checkValid(String str,String type){
        if(org.apache.commons.lang3.StringUtils.isNotBlank(type)){
            //开始校验
            if(Const.USERNAME.equals(type)){
                int resCount=userMapper.checkUserName(str);
                if(resCount>0){
                    return ServerResponse.createByError(RegisterEnum.USERNAME_EXIST.getMsg());
                }
            }
            if(Const.EMAIL.equals(type)){
                int resCount=userMapper.checkEmail(str);
                if(resCount>0){
                    return ServerResponse.createByError(RegisterEnum.EMAIL_EXIST.getMsg());
                }
            }
        }else {
            return ServerResponse.createByError(RegisterEnum.PARAMETER_ERROR.getMsg());
        }

        return ServerResponse.createBySuccessMessage("校验成功");
    }

    public ServerResponse selectQuestion(String username){
        ServerResponse validResponse=this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByError(LoginEnum.NO_USER.getMsg());
        }
        String question=userMapper.selectQuestionByUsername(username);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByError("找回密码的问题为空");
    }

    public ServerResponse<String> checkAnswer(String username,String answer,String question){
        int resCount=userMapper.checkAnswer(username,answer,question);
        if(resCount>0){
            //说明问题和答案是这个用户的
            String forgetToken= UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKENKEY+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByError("问题与答案不匹配");
    }

    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByError("参数错误，token需要传递");
        }
        ServerResponse vaildResponse = this.checkValid(username,Const.USERNAME);
        if(vaildResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByError("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKENKEY+username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByError("token无效");
        }
        if(StringUtils.equals(token,forgetToken)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswdByUserName(username,md5Password);
            if(rowCount>0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else {
            return ServerResponse.createByError("token错误，请重新获取修改密码需要的token");
        }
        return ServerResponse.createByError("修改密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordNew, String passwordOld,User user) {
        //防止横向越权，要校验用户的旧密码
        int resCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resCount == 0){
            return ServerResponse.createByError("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }else
            return ServerResponse.createByError("密码更新失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user){
        //username是不能被更新的
        //email也要进行一个校验,校验新的email是不是已经存在,并且存在的email如果相同的话,不能是我们当前的这个用户的.
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServerResponse.createByError("email已存在,请更换email再尝试更新");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByError("更新个人信息失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByError("找不到当前用户");
        }
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);

    }

    /**
     * 校验是否是管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }


}
