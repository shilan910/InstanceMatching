package com.zqh;

import javax.xml.transform.dom.DOMLocator;
import java.util.Comparator;

/**
 * Created by sl on 2017/3/1.
 */
public class Result implements Comparable {

    private String entity1;
    private String entity2;
//    private String relation;
//    private String datatype;
    private String measure;

    public Result(String entity1, String entity2, String measure) {
        this.entity1 = entity1;
        this.entity2 = entity2;
//        this.relation = relation;
//        this.datatype = datatype;
        this.measure = measure;
    }

    public int compareTo(Object o1) {
        Result r1 = (Result)o1;
        double r1_m = Double.valueOf(r1.getMeasure());
        return Double.valueOf(this.measure).compareTo(r1_m);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        if (entity1 != null ? !entity1.equals(result.entity1) : result.entity1 != null) return false;
        if (entity2 != null ? !entity2.equals(result.entity2) : result.entity2 != null) return false;
//        if (relation != null ? !relation.equals(result.relation) : result.relation != null) return false;
//        if (datatype != null ? !datatype.equals(result.datatype) : result.datatype != null) return false;
        return measure != null ? measure.equals(result.measure) : result.measure == null;
    }

    @Override
    public int hashCode() {
        int result = entity1 != null ? entity1.hashCode() : 0;
        result = 31 * result + (entity2 != null ? entity2.hashCode() : 0);
//        result = 31 * result + (relation != null ? relation.hashCode() : 0);
//        result = 31 * result + (datatype != null ? datatype.hashCode() : 0);
        result = 31 * result + (measure != null ? measure.hashCode() : 0);
        return result;
    }



    public String getEntity1() {
        return entity1;
    }

    public void setEntity1(String entity1) {
        this.entity1 = entity1;
    }

    public String getEntity2() {
        return entity2;
    }

    public void setEntity2(String entity2) {
        this.entity2 = entity2;
    }


    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }
}
