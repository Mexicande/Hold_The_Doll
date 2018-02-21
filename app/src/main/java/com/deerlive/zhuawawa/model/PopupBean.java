package com.deerlive.zhuawawa.model;

import java.io.Serializable;

/**
 * Created by apple on 2018/2/21.
 */

public class PopupBean implements Serializable {
    private String name;
    private String description;
    private String status;
    private String img;
    private String size;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
