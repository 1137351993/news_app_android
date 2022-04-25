package com.example.gotitapplication.attention;

public class GroupBean {

    private String name;
    private int package_id;

    public GroupBean() {
    }

    public GroupBean(int package_id, String name) {
        this.package_id=package_id;
        this.name = name;
    }

    public int getPackage_id() {
        return package_id;
    }

    public void setPackage_id(int package_id) {
        this.package_id = package_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}