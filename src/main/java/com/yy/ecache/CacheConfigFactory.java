package com.yy.ecache;

import com.ecache.CacheConfig;

/**
 * @author 谢俊权
 * @create 2016/8/10 10:49
 */
public class CacheConfigFactory {

    private int defaultExpiredSeconds = 60 * 60 * 24;

    private int schedulerCorePoolSize = 64;

    private int retryRegisterMSeconds = 1000 * 2;

    private int lockSegments = 32;

    private boolean lockIsFair = false;

    private boolean avoidServerOverload = false;


    public CacheConfig create(){
        CacheConfig config = new CacheConfig.Builder()
                .defaultExpiredSeconds(defaultExpiredSeconds)
                .schedulerCorePoolSize(schedulerCorePoolSize)
                .retryRegisterMSeconds(retryRegisterMSeconds)
                .lockSegments(lockSegments)
                .lockIsFair(lockIsFair)
                .avoidServerOverload(avoidServerOverload)
                .build();
        return config;
    }

    public void setDefaultExpiredSeconds(int defaultExpiredSeconds) {
        this.defaultExpiredSeconds = defaultExpiredSeconds;
    }

    public void setSchedulerCorePoolSize(int schedulerCorePoolSize) {
        this.schedulerCorePoolSize = schedulerCorePoolSize;
    }

    public void setRetryRegisterMSeconds(int retryRegisterMSeconds) {
        this.retryRegisterMSeconds = retryRegisterMSeconds;
    }

    public void setLockSegments(int lockSegments) {
        this.lockSegments = lockSegments;
    }

    public void setLockIsFair(boolean lockIsFair) {
        this.lockIsFair = lockIsFair;
    }

    public void setAvoidServerOverload(boolean avoidServerOverload) {
        this.avoidServerOverload = avoidServerOverload;
    }
}
