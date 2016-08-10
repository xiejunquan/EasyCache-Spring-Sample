package com.yy.ecache.model;

/**
 * @author 谢俊权
 * @create 2016/8/3 15:20
 */
public class UserInfo {

    private int id;
    private String userName;
    private String password;


    public UserInfo() {
    }

    public UserInfo(int id, String userName, String password) {
        this.id = id;
        this.userName = userName;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
