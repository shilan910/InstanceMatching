package com.zqh;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sl on 2017/3/1.
 */
public class XMLHelper {

    private String fileName;

    public XMLHelper(String fileName){
        this.fileName = fileName;
    }

    public static void main(String args[]){
        XMLHelper xmlHelper = new XMLHelper("D:\\data\\new\\nytimes-mappings-split\\nyt-dbpedia-people-mappings.rdf");
        try {
            List<Result> results = xmlHelper.run();
            for(Result result : results){
                System.out.println(result.getEntity1());
                System.out.println(result.getEntity2());
                System.out.println(result.getMeasure()+"\n");
            }
            System.out.println(results.size());

        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public List<Result> run() throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(fileName));
        Element root = document.getRootElement();

        List<Result> results = new ArrayList<Result>();
        listNodes(root,results);
        return results;
    }


    //遍历当前节点下的所有节点
    private void listNodes(Element node , List<Result> results){
        if(node.getName().equals("Cell")){
            results.add(new Result( node.element("entity1").attributeValue("resource"),
                                    node.element("entity2").attributeValue("resource"),
                                    node.element("measure").getData().toString()));
        }
        Iterator<Element> iterator = node.elementIterator();
        while(iterator.hasNext()){
            Element e = iterator.next();
            listNodes(e,results);
        }
    }


}
