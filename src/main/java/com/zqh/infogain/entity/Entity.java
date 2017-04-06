package com.zqh.infogain.entity;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/4.
 */
public class Entity {

    private String subject;
    private List<String> predicate_object;
    private Map<String,List<String>> predicate_objects;

    public Entity(String subject, Map<String, List<String>> predicate_objects) {
        this.subject = subject;
        this.predicate_objects = predicate_objects;
    }

    public Map<String, List<String>> getPredicate_objects() {
        return predicate_objects;
    }

    public void setPredicate_objects(Map<String, List<String>> predicate_objects) {
        this.predicate_objects = predicate_objects;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getPredicate_object() {
        return predicate_object;
    }

    public void setPredicate_object(List<String> predicate_object) {
        this.predicate_object = predicate_object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;

        if (!subject.equals(entity.subject)) return false;
        return predicate_objects.equals(entity.predicate_objects);
    }

    @Override
    public int hashCode() {
        int result = subject.hashCode();
        result = 31 * result + predicate_objects.hashCode();
        return result;
    }
}
