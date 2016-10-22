package com.ecache.test.service;

import com.ecache.MissCacheHandler;
import com.ecache.RemoteCache;
import com.ecache.CacheType;
import com.ecache.test.dao.PageDao;
import com.ecache.test.model.PageData;
import com.ecache.test.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author 谢俊权
 * @create 2016/8/3 16:20
 */
@Service
public class PageServiceInject {

    @Autowired
    private PageDao pageDao;

    @Autowired
    private RemoteCache remoteCache;

    public PageData<UserInfo> page(String biz, int moduleId){
        String key = "PageServiceInject|page|"+biz+"|"+moduleId;
        PageData<UserInfo> pageData = remoteCache.get(key, new CacheType<PageData<UserInfo>>(){});
        if(pageData == null){
            long seconds = System.currentTimeMillis()/1000;
            System.out.println(seconds + " : " + "page from dao");
            pageData = pageDao.page(biz, moduleId);
            if(pageData != null){
                remoteCache.set(key, pageData, 60);
            }
        }
        return pageData;
    }

    public Map<String , PageData<UserInfo>> pageMap(String biz, int moduleId){
        String key = "PageServiceInject|pageMap|"+biz+"|"+moduleId;
        Map<String , PageData<UserInfo>> pageDataMap = remoteCache.get(
                key,
                60,
                new CacheType<Map<String , PageData<UserInfo>>>() {},
                new MissCacheHandler<Map<String , PageData<UserInfo>>>(biz, moduleId) {
                    @Override
                    public Map<String , PageData<UserInfo>> getData() {
                        String biz = (String) params.get(0);
                        int moduleId = (int) params.get(1);
                        long seconds = System.currentTimeMillis()/1000;
                        System.out.println(seconds + " : " + "pageMap from dao");
                        return pageDao.pageMap(biz, moduleId);
                }
        });
        return pageDataMap;
    }

    public List<UserInfo> list(String biz, int moduleId, String key){
        String cacheKey = "PageServiceInject|list|" + key;
        List<UserInfo> list = remoteCache.get(
                cacheKey,
                60,
                new CacheType<List<UserInfo>>() {},
                new MissCacheHandler<List<UserInfo>>(biz, moduleId) {
                    @Override
                    public List<UserInfo> getData() {
                        String biz = (String) params.get(0);
                        int moduleId = (int) params.get(1);
                        long seconds = System.currentTimeMillis()/1000;
                        System.out.println(seconds + " : " + "list from dao");
                        return pageDao.page(biz, moduleId).getData();
                    }
                }
        );

        return list;
    }
}
