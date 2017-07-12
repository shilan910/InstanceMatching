package com.oaei2017.entity;

/**
 * Created by Administrator on 2017/7/11.
 */
public class Result {
    private String traceName1;
    private String traceName2;

    public Result(String traceName1, String traceName2) {
        this.traceName1 = traceName1;
        this.traceName2 = traceName2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        if (!traceName1.equals(result.traceName1)) return false;
        return traceName2.equals(result.traceName2);
    }

    @Override
    public int hashCode() {
        int result = traceName1.hashCode();
        result = 31 * result + traceName2.hashCode();
        return result;
    }

    public String getTraceName1() {
        return traceName1;
    }

    public void setTraceName1(String traceName1) {
        this.traceName1 = traceName1;
    }

    public String getTraceName2() {
        return traceName2;
    }

    public void setTraceName2(String traceName2) {
        this.traceName2 = traceName2;
    }
}
