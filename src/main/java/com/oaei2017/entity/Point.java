package com.oaei2017.entity;

import java.util.Date;

/**
 * Created by Administrator on 2017/7/9.
 */
public class Point {
    private String pointName;
    private double longitude;
    private double latitude;
    private double speed;
    private long dateTime;

    public Point(){

    }

    public Point(String pointName) {
        this.pointName = pointName;
    }

    public Point(String pointName, double longitude, double latitude, long dateTime) {
        this.pointName = pointName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.dateTime = dateTime;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }
}
