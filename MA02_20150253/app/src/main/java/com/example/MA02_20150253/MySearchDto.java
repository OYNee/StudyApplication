package com.example.MA02_20150253;

import android.text.Html;
import android.text.Spanned;


public class MySearchDto {
    private int _id;
    private String title;       //제목
    private String description;        //내용 요약
    private String link;     //백과사전 링크

    public MySearchDto() {
    }

    public MySearchDto(int _id, String title, String description, String link) {
        this._id = _id;
        this.title = title;
        this.description = description;
        this.link = link;
    }

    public int get_id() {
        return _id;
    }

    public String getTitle() {
        Spanned spanned = Html.fromHtml(title);
        return spanned.toString();
    }

    public String getDescription() {
        Spanned spanned = Html.fromHtml(description);
        return spanned.toString();
    }

    public String getLink() {
        return link;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLink(String link) {
        this.link = link;
    }



    @Override
    public String toString() {
        return "Place{" + "title = " + getTitle() +
                ", description = " + description + '\'' +
                ", link = " + link + '\'' +
                '}';
    }
}
