package com.zqh.FPGrowth.example;

import java.util.List;

/**
 * Created by Administrator on 2017/3/22.
 */
public class FP_Item {
    private List<String> items;
    private int count;
    private double weight;

    public FP_Item(List<String> items, int count) {
        this.items = items;
        this.count = count;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FP_Item fp_item = (FP_Item) o;

        return items != null ? items.equals(fp_item.items) : fp_item.items == null;
    }

    @Override
    public int hashCode() {
        return items != null ? items.hashCode() : 0;
    }
}
