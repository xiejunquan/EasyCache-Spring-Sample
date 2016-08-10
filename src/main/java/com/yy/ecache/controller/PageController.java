package com.yy.ecache.controller;

import com.alibaba.fastjson.JSONObject;
import com.yy.ecache.model.PageData;
import com.yy.ecache.model.UserInfo;
import com.yy.ecache.service.PageService;
import com.yy.ecache.service.PageServiceInject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author 谢俊权
 * @create 2016/8/3 15:44
 */
@Controller
public class PageController {

    @Autowired
    private PageService pageService;

    @Autowired
    private PageServiceInject pageServiceInject;

    @RequestMapping("/page")
    public @ResponseBody String page(String biz, int moduleId){
        JSONObject json = new JSONObject();
        PageData<UserInfo> pageData = pageService.page(biz, moduleId);
        json.put("page", pageData);
        return json.toJSONString();
    }

    @RequestMapping("/pageMap")
    public @ResponseBody String pageMap(String biz, int moduleId){
        JSONObject json = new JSONObject();
        Map<String, PageData<UserInfo>> pageMap = pageService.pageMap(biz, moduleId);
        json.put("page", pageMap);
        return json.toJSONString();
    }

    @RequestMapping("/list")
    public  @ResponseBody String list(String biz, int moduleId){
        JSONObject json = new JSONObject();
        String key = "page|list|"+biz;
        List<UserInfo> list = pageService.list(biz, moduleId, key);
        json.put("page", list);
        return json.toJSONString();
    }


    @RequestMapping("/pageInject")
    public  @ResponseBody String pageInject(String biz,int moduleId){
        JSONObject json = new JSONObject();
        PageData<UserInfo> pageData = pageServiceInject.page(biz, moduleId);
        json.put("page", pageData);
        return json.toJSONString();
    }

    @RequestMapping("/pageMapInject")
    public  @ResponseBody String pageMapInject(String biz, int moduleId){
        JSONObject json = new JSONObject();
        Map<String, PageData<UserInfo>> pageMap = pageServiceInject.pageMap(biz, moduleId);
        json.put("page", pageMap);
        return json.toJSONString();
    }

    @RequestMapping("/listInject")
    public  @ResponseBody String listInject(String biz, int moduleId){
        JSONObject json = new JSONObject();
        String key = "page|list|"+biz;
        List<UserInfo> list = pageServiceInject.list(biz, moduleId, key);
        json.put("page", list);
        return json.toJSONString();
    }

}
