package com.yy.ecache.model;

import java.util.List;

/**
 * @author 谢俊权
 * @create 2016/8/3 15:19
 */
public class PageData<T> {

    private int totalCount;

    private List<T> data;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
