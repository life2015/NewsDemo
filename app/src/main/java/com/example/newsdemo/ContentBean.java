package com.example.newsdemo;

/**
 * Created by 冀辰阳 on 2016/3/23.
 */
public class ContentBean {
    String subject;
    String content;
    //新闻来源，供稿，审要，摄影
    String newscome;
    String gonggao;
    String shenggao;
    String sheying;
    int visitcount;

    public ContentBean() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNewscome() {
        return newscome;
    }

    public void setNewscome(String newscome) {
        this.newscome = newscome;
    }

    public String getGonggao() {
        return gonggao;
    }

    public void setGonggao(String gonggao) {
        this.gonggao = gonggao;
    }

    public String getShenggao() {
        return shenggao;
    }

    public void setShenggao(String shenggao) {
        this.shenggao = shenggao;
    }

    public String getSheying() {
        return sheying;
    }

    public void setSheying(String sheying) {
        this.sheying = sheying;
    }

    public int getVisitcount() {
        return visitcount;
    }

    public void setVisitcount(int visitcount) {
        this.visitcount = visitcount;
    }
}
