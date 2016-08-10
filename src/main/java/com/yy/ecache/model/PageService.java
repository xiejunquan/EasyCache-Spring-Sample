package com.yy.ecache.model;

import com.ecache.annotation.LocalCache;
import org.springframework.stereotype.Service;

/**
 * @author 谢俊权
 * @create 2016/8/3 15:18
 */
@Service
public class PageService {

    @LocalCache(key = "$1$2", expire = 60)
    public PageData<UserInfo> page(String biz, int moduleId){
        long seconds = System.currentTimeMillis()/1000;
        System.out.println(seconds + " : " + "page from model dao");
        return null;
    }
}
