package com.deerlive.zhuawawa.model;

import com.bigkoo.pickerview.model.IPickerViewData;

import java.io.Serializable;
import java.util.List;

/**
 * Created by apple on 2018/2/14.
 */

public class CityEntityBean implements IPickerViewData,Serializable {

    /**
     * city : [{"area":["东城区","西城区","崇文区","宣武区","朝阳区","丰台区","石景山区","海淀区","门头沟区","房山区","通州区","顺义区","昌平区","大兴区","平谷区","怀柔区","密云县","延庆县"],"name":"北京市"}]
     * name : 北京市
     */

    private String name;
    private List<CityBean> city;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CityBean> getCity() {
        return city;
    }

    public void setCity(List<CityBean> city) {
        this.city = city;

    }

    @Override
    public String getPickerViewText() {
        return this.name;
    }


    public static class CityBean implements Serializable {
        /**
         * area : ["东城区","西城区","崇文区","宣武区","朝阳区","丰台区","石景山区","海淀区","门头沟区","房山区","通州区","顺义区","昌平区","大兴区","平谷区","怀柔区","密云县","延庆县"]
         * name : 北京市
         */

        private String name;
        private List<String> area;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getArea() {
            return area;
        }

        public void setArea(List<String> area) {
            this.area = area;
        }

        @Override
        public String toString() {
            return "CityBean{" +
                    "name='" + name + '\'' +
                    ", area=" + area +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "CityEntityBean{" +
                "name='" + name + '\'' +
                ", city=" + city +
                '}';
    }


}
