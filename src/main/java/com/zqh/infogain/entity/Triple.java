package com.zqh.infogain.entity;

/**
 * Created by Administrator on 2017/4/27.
 */
public class Triple {
    private String subject;
    private String property;
    private String object;

    public Triple(String subject, String property, String object) {
        this.subject = subject;
        this.property = property;
        this.object = object;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Triple triple = (Triple) o;

        if (subject != null ? !subject.equals(triple.subject) : triple.subject != null) return false;
        if (property != null ? !property.equals(triple.property) : triple.property != null) return false;
        return object != null ? object.equals(triple.object) : triple.object == null;
    }

    @Override
    public int hashCode() {
        int result = subject != null ? subject.hashCode() : 0;
        result = 31 * result + (property != null ? property.hashCode() : 0);
        result = 31 * result + (object != null ? object.hashCode() : 0);
        return result;
    }
}
