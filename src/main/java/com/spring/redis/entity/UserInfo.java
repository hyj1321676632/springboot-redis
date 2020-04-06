package com.spring.redis.entity;

import java.io.Serializable;
import java.util.Date;

public class UserInfo implements Serializable {
    private long id;
    private String userId;
    private String userPassword;
    private String userName;
    private String userSchool;
    private String userSex;
    private String userRole;
    private Date createDate;

    public UserInfo() {
    }

    public UserInfo(long id, String userId, String userPassword, String userName, String userSchool, String userSex, String userRole, Date createDate) {
        this.id = id;
        this.userId = userId;
        this.userPassword = userPassword;
        this.userName = userName;
        this.userSchool = userSchool;
        this.userSex = userSex;
        this.userRole = userRole;
        this.createDate = createDate;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSchool() {
        return userSchool;
    }

    public void setUserSchool(String userSchool) {
        this.userSchool = userSchool;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}