package com.deerlive.zhuawawa.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/19.
 * Author: XuDeLong
 */

public class PayModel implements Serializable{
    private String id;
    private String name;
    private String type;
    private String icon;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
