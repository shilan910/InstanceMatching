package com.zqh;

import java.util.Map;

/**
 * Created by sl on 2017/2/28.
 */
public class Entity {

    private String subject;
    private Map<String,String> predicate_object;

    public Entity(String subject , Map<String,String> predicate_object) {

        this.subject = subject;
        this.predicate_object = predicate_object;
    }

    public Entity(){

    }


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Map<String, String> getPredicate_object() {
        return predicate_object;
    }

    public void setPredicate_object(Map<String, String> predicate_object) {
        this.predicate_object = predicate_object;
    }
}
