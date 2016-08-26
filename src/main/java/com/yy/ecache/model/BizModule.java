package com.yy.ecache.model;

/**
 * @author 谢俊权
 * @create 2016/8/26 18:15
 */
public class BizModule {

    private String biz;
    private int moduleId;

    public BizModule(String biz, int moduleId) {
        this.biz = biz;
        this.moduleId = moduleId;
    }

    public String getBiz() {
        return biz;
    }

    public int getModuleId() {
        return moduleId;
    }
}
