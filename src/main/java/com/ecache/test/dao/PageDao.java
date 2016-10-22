package com.ecache.test.dao;


import com.ecache.test.model.PageData;
import com.ecache.test.model.UserInfo;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 谢俊权
 * @create 2016/8/3 15:28
 */
@Repository
public class PageDao {

    private Map<String, PageData<UserInfo>> bizModuleMap = new HashMap<>();
    {
        List<UserInfo> list1 = new ArrayList<>();
        list1.add(new UserInfo(1, "1", "1"));
        list1.add(new UserInfo(2, "2", "2"));
        PageData<UserInfo> pageData1 = new PageData<>();
        pageData1.setData(list1);
        pageData1.setTotalCount(2);

        List<UserInfo> list2 = new ArrayList<>();
        list2.add(new UserInfo(3, "3", "3"));
        list2.add(new UserInfo(4, "4", "4"));
        PageData<UserInfo> pageData2 = new PageData<>();
        pageData2.setData(list2);
        pageData2.setTotalCount(2);

        bizModuleMap.put("biz1:1", pageData1);
        bizModuleMap.put("biz2:2", pageData2);
    }

    public PageData<UserInfo> page(String biz, int moduleId){
        return bizModuleMap.get(biz + ":" + moduleId);
    }

    public Map<String, PageData<UserInfo>> pageMap(String biz, int moduleId){
        Map<String, PageData<UserInfo>> map = new HashMap<>();
        String key = biz + ":" + moduleId;
        map.put(key, bizModuleMap.get(key));
        return map;
    }
}
