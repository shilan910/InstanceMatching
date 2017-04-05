package com.zqh.FPGrowth.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/21.
 */
public class FP_TreeNode implements Comparable<FP_TreeNode> {
    private String nodeName;//节点名称
    private int count;//计数
    private FP_TreeNode parent;//父节点
    private List<FP_TreeNode> children;
    private FP_TreeNode nextNode;//下一个同名节点

    public FP_TreeNode(){}

    public FP_TreeNode(String name){
        nodeName=name;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public FP_TreeNode getParent() {
        return parent;
    }

    public void setParent(FP_TreeNode parent) {
        this.parent = parent;
    }

    public List<FP_TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<FP_TreeNode> children) {
        this.children = children;
    }

    public FP_TreeNode getNextNode() {
        return nextNode;
    }

    public void setNextNode(FP_TreeNode nextNode) {
        this.nextNode = nextNode;
    }
    //添加孩子节点
    public void addChild(FP_TreeNode child){
        if(this.getChildren()==null){//孩子节点为空，新建链表，加入孩子节点，再作为该节点的孩子结合
            List<FP_TreeNode> list=new ArrayList<FP_TreeNode>();
            list.add(child);
            this.setChildren(list);
        }
        else{
            this.getChildren().add(child);
        }
    }

    //查找孩子节点
    public FP_TreeNode findChild(String name){
        List<FP_TreeNode> children=this.getChildren();
        if(children!=null){
            for(FP_TreeNode child:children){
                if(child.getNodeName().equals(name)){
                    return child;
                }
            }
        }
        return null;
    }

    //打印孩子节点的名称
    public void printChildrenName(){
        List<FP_TreeNode> children=this.getChildren();
        if(children!=null){
            for(FP_TreeNode child : children){
                System.out.print(child.getNodeName()+" ");
            }
        }
        else {
            System.out.println("null");
        }
    }


    public void countCreament(int n){
        this.count+=n;
    }

    //使得Arrays.sort()按照降序排列
//    @Override
    public int compareTo(FP_TreeNode node){
        int count=node.getCount();
        return count-this.count;
    }

}
