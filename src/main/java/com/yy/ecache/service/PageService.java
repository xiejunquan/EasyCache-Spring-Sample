package com.yy.ecache.service;

import com.ecache.annotation.Cache;
import com.ecache.annotation.LocalCache;
import com.ecache.annotation.RemoteCache;
import com.yy.ecache.RedisCache;
import com.yy.ecache.dao.PageDao;
import com.yy.ecache.model.BizModule;
import com.yy.ecache.model.PageData;
import com.yy.ecache.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author 谢俊权
 * @create 2016/8/3 15:18
 */
@Service
public class PageService {

    @Autowired
    private PageDao pageDao;

    @LocalCache(key = "$1.biz $1.moduleId", expire = 60)
    public PageData<UserInfo> page(BizModule bm){
        long seconds = System.currentTimeMillis()/1000;
        System.out.println(seconds + " : " + "page from dao");
        return pageDao.page(bm.getBiz(), bm.getModuleId());
    }

    @RemoteCache(key = "$1$2", expire = 60)
    public Map<String , PageData<UserInfo>> pageMap(String biz, int moduleId){
        long seconds = System.currentTimeMillis()/1000;
        System.out.println(seconds + " : " + "pageMap from dao");
        return pageDao.pageMap(biz, moduleId);
    }

    @Cache(instance = RedisCache.class, key = "$3", expire = 60)
    public List<UserInfo> list(String biz, int moduleId, String key){
        long seconds = System.currentTimeMillis()/1000;
        System.out.println(seconds + " : " + "list from dao");
        return pageDao.page(biz, moduleId).getData();
    }

}
