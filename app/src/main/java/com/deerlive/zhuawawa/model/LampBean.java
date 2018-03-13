package com.deerlive.zhuawawa.model;

import java.util.List;

/**
 * Created by apple on 2018/3/13.
 * 跑马灯
 */

public class LampBean {

    /**
     * code : 200
     * info : [{"content":"为了保障您的良好休息，快来抓娃娃开放时间为每天早上10点-晚10点，抓中申请发货将在3个工作日内完成配送，祝您玩的愉快~"},{"content":"为了保障您的良好休息，快来抓娃娃开放时间为每天早上10点-晚10点，抓中申请发货,将在3个工作日内完成配送，祝您玩的愉快~"}]
     * tourist : [{"name":"寡人如拥"},{"name":"时光偷走当初"},{"name":"浮光初槿花落"},{"name":"不谈爱情"},{"name":"昨日雾浓"},{"name":"夜盲症"},{"name":"旧街古人"},{"name":"孤久则惯"},{"name":"冷拥"},{"name":"栀子花ゝ"},{"name":"陌゛雨涵"},{"name":"残梦▌ Dangerou"},{"name":"wirepuller 阴谋者"},{"name":"?离烟 °ine"},{"name":"小代言ヽ"},{"name":"纯洁美青柠 ·η"},{"name":"╰苏染"},{"name":"不勉强不将就"},{"name":"擦去眼泪继续笑"},{"name":"梦醒因你"},{"name":"再酷也只撩你."},{"name":"一身祖宗味儿"},{"name":"怪我性子野"},{"name":"四野无人"},{"name":"没有故事"},{"name":"阳光回忆."},{"name":"满分是你"},{"name":"清风挽发"}]
     * rand : 23
     * descrp : success
     */

    private int code;
    private int rand;
    private String descrp;
    private List<InfoBean> info;
    private List<TouristBean> tourist;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getRand() {
        return rand;
    }

    public void setRand(int rand) {
        this.rand = rand;
    }

    public String getDescrp() {
        return descrp;
    }

    public void setDescrp(String descrp) {
        this.descrp = descrp;
    }

    public List<InfoBean> getInfo() {
        return info;
    }

    public void setInfo(List<InfoBean> info) {
        this.info = info;
    }

    public List<TouristBean> getTourist() {
        return tourist;
    }

    public void setTourist(List<TouristBean> tourist) {
        this.tourist = tourist;
    }

    public static class InfoBean {
        /**
         * content : 为了保障您的良好休息，快来抓娃娃开放时间为每天早上10点-晚10点，抓中申请发货将在3个工作日内完成配送，祝您玩的愉快~
         */

        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class TouristBean {
        /**
         * name : 寡人如拥
         */

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
