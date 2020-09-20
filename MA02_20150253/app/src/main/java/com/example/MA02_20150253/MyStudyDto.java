package com.example.MA02_20150253;
import java.io.Serializable;


public class MyStudyDto implements Serializable{
    private int _id;
    private int img;
    private String name;
    private String percentage;

    public MyStudyDto(int _id, int img, String name, String percentage) {
        this._id = _id;
        this.img = img;
        this.name = name;
        this.percentage = percentage;
    }

    public int get_id() {
        return _id;
    }

    public int getImg() {
        return img;
    }

    public String getName() {
        return name;
    }

    public  String getPercentage() {
        return percentage;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }
}
