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
//        System.out.println("当前节点的名称：" + node.getName());

        if(node.getName().equals("Cell")){


            results.add(new Result( node.element("entity1").attributeValue("resource"),
                                    node.element("entity2").attributeValue("resource"),
                                    node.element("measure").getData().toString()));

//            System.out.println("\n"+node.element("entity1").attributeValue("resource"));
//            System.out.println(node.element("entity2").attributeValue("resource"));
//            System.out.println(node.element("relation").getData().toString());
//            System.out.println(node.element("measure").attributeValue("datatype"));
//            System.out.println(node.element("measure").getData().toString()+"\n");
        }


        Iterator<Element> iterator = node.elementIterator();
        while(iterator.hasNext()){
            Element e = iterator.next();
            listNodes(e,results);
        }
    }


}
