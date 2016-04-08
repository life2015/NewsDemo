package com.example.newsdemo;

import android.graphics.Bitmap;

/**
 * Created by 冀辰阳 on 2016/3/23.
 */
public class NewsBean
{
    int index;
    String subject;
    String pic;
    int visitCount;
    int comments;
    String summary;
    int TYPE;

    public NewsBean() {
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public NewsBean(int index, String subject, String pic, int visitCount, int comments, String summary) {

        this.index = index;
        this.subject = subject;
        this.pic = pic;
        this.visitCount = visitCount;
        this.comments = comments;
        this.summary = summary;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "NewsBean{" +
                "index=" + index +
                ", subject='" + subject + '\'' +
                ", visitCount=" + visitCount +
                ", comments=" + comments +
                ", summary='" + summary + '\'' +
                '}';
    }
}
