package com.example.flowerapplication;

public class FlowerItem {
    private long ID;
    private String imgEncode;
    private String name;

    public FlowerItem(long id, String imgEncode, String name) {
        this.ID = id;
        this.imgEncode = imgEncode;
        this.name = name;
    }

    public Long getID() {
        return ID;
    }
    public String getImgEncode() {
        return imgEncode;
    }
    public String getName() {
        return name;
    }
}
