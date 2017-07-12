package com.zqh.infogain.entity;

/**
 * Created by Administrator on 2017/4/4.
 */
public class PropertyPair {

    private String property1;
    private String property2;
    private double ig;

    public PropertyPair(String property1, String property2) {
        this.property1 = property1;
        this.property2 = property2;
    }

    public PropertyPair(String property1, String property2, double ig) {
        this.property1 = property1;
        this.property2 = property2;
        this.ig = ig;
    }

    public String getProperty1() {
        return property1;
    }

    public void setProperty1(String property1) {
        this.property1 = property1;
    }

    public String getProperty2() {
        return property2;
    }

    public void setProperty2(String property2) {
        this.property2 = property2;
    }

    public double getIg() {
        return ig;
    }

    public void setIg(double ig) {
        this.ig = ig;
    }
}
