package com.example.MA02_20150253;

import java.io.Serializable;


public class MyDailyStudyDto implements Serializable {
    private int _id;
    private int img;
    private String title;

    public MyDailyStudyDto() {
        super();
    }

    public MyDailyStudyDto(int _id, int img, String title) {
        this._id = _id;
        this.img = img;
        this.title = title;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }
}
