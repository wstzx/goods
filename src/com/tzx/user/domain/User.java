package com.tzx.user.domain;

/**
 * 用户模块实体类
 */
//映射表:t_user    注意:需要找到需要该模块实体类的所有表单,防止数据丢失(如果足够复杂,每一层都需要有自己的实体类)
public class User {

    //对应数据库表
    private String uid; //主键
    private String loginname;   //登录名
    private String loginpass;   //登录密码
    private String email;       //邮箱

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    private boolean status;         //激活状态
    private String activationCode;  //激活码(唯一值)

    //对应注册表单
    private String reloginpass;    //确认密码

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", loginname='" + loginname + '\'' +
                ", loginpass='" + loginpass + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", activationCode='" + activationCode + '\'' +
                ", reloginpass='" + reloginpass + '\'' +
                ", verifyCode='" + verifyCode + '\'' +
                ", newloginpass='" + newloginpass + '\'' +
                '}';
    }

    public String getReloginpass() {
        return reloginpass;
    }

    public void setReloginpass(String reloginpass) {
        this.reloginpass = reloginpass;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getNewloginpass() {
        return newloginpass;
    }

    public void setNewloginpass(String newloginpass) {
        this.newloginpass = newloginpass;
    }

    private String verifyCode;     //验证码

    //修改密码表单
    private String newloginpass;   //新密码


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getLoginpass() {
        return loginpass;
    }

    public void setLoginpass(String loginpass) {
        this.loginpass = loginpass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

}
