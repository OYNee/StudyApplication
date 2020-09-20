package com.example.MA02_20150253;

import android.text.Html;
import android.text.Spanned;

public class MyLocation {
    private int _id;
    private String title;
    private String address;
    private double latitude;
    private double longitude;

    public MyLocation() {
        super();
    }

    public MyLocation(String title, String address) {
        this.title = title;
        this.address = address;
    }

    public int get_id() {
        return _id;
    }

    public String getTitle() {
        Spanned spanned = Html.fromHtml(title);
        return spanned.toString();
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
