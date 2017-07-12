package com.zqh.infogain.finder;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/26.
 */
public class EntityForLinking {

    private String subject;
    private Map<String,List<String>> p_os;

    public EntityForLinking(String subject, Map<String, List<String>> p_os) {
        this.subject = subject;
        this.p_os = p_os;
    }

    public String getSubject() {
        return subject;
    }

    public Map<String, List<String>> getP_os() {
        return p_os;
    }
}
