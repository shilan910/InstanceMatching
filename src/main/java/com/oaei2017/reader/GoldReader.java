package com.oaei2017.reader;

import com.oaei2017.entity.Result;
import org.apache.jena.rdf.model.Model;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2017/7/11.
 */
public class GoldReader {
    private static Model model;
    private String filename;
    private List<Result> results;

    public GoldReader(String filename) {
        this.filename = filename;
        results = new ArrayList<Result>();
        run();
    }

    public List<Result> getResults() {
        return results;
    }

    private void run(){
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(new File(filename));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = document.getRootElement();
        listNodes(root,results);
    }


    //遍历当前节点下的所有节点
    private void listNodes(Element node , List<Result> results){
        if(node.getName().equals("Cell")){
            results.add(new Result( node.element("entity1").attributeValue("resource"),
                    node.element("entity2").attributeValue("resource")));
        }
        Iterator<Element> iterator = node.elementIterator();
        while(iterator.hasNext()){
            Element e = iterator.next();
            listNodes(e,results);
        }
    }

    public static void main(String args[]) {
        GoldReader goldReader = new GoldReader("C:/Users/Administrator/Desktop/OAEI2017/data/FORTH_sandbox/Linking/refalign.rdf");
        List<Result> results = goldReader.getResults();
        System.out.println(results.size());
        for (Result result : results) {
            System.out.println(result.getTraceName1());
            System.out.println(result.getTraceName2() + "\n");
        }
    }
}