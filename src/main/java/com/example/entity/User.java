package com.example.entity;

/**
 * Description: 用户信息类
 */
public class User {

    private String account;
    private String name;
    private String password;
    private int userId;
    private int sex;
    private int age;
    private String area;

    public User() {
        this.setAccount(null);
        this.setPassword(null);
        this.setName(null);
        this.setUserId(-1);
        this.setSex(0);
        this.setAge(0);
        this.setArea(null);
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "\nName:" + this.name +
                "\nId:" + this.userId +
                "\nAccount:" + this.account +
                "\nPassword" + this.password;

    }
}
