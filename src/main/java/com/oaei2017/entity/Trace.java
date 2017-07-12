package com.oaei2017.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/7/10.
 */
public class Trace {

    private String traceName;
    private List<Point> points;

    public Trace(){}

    public Trace(String traceName, List<Point> points) {
        this.traceName = traceName;
        this.points = points;
    }

    public String getTraceName() {
        return traceName;
    }

    public void setTraceName(String traceName) {
        this.traceName = traceName;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public void addPoint(Point point){

    }
}
