package com.yy.ecache;

import com.ecache.CacheConfig;

/**
 * @author 谢俊权
 * @create 2016/8/10 10:49
 */
public class CacheConfigFactory {

    private int defaultExpiredSeconds = 60 * 60;

    private int schedulerCorePoolSize = 64;

    private int lockSegments = 32;

    private boolean lockIsFair = false;

    private boolean avoidServerOverload = false;

    private int clearSchedulerIntervalSeconds = 60 * 60 * 24;


    public CacheConfig create(){
        CacheConfig config = new CacheConfig.Builder()
                .defaultExpiredSeconds(defaultExpiredSeconds)
                .schedulerCorePoolSize(schedulerCorePoolSize)
                .lockSegments(lockSegments)
                .lockIsFair(lockIsFair)
                .avoidServerOverload(avoidServerOverload)
                .clearSchedulerIntervalSeconds(clearSchedulerIntervalSeconds)
                .build();
        return config;
    }

    public void setDefaultExpiredSeconds(int defaultExpiredSeconds) {
        this.defaultExpiredSeconds = defaultExpiredSeconds;
    }

    public void setSchedulerCorePoolSize(int schedulerCorePoolSize) {
        this.schedulerCorePoolSize = schedulerCorePoolSize;
    }

    public void setClearSchedulerIntervalSeconds(int clearSchedulerIntervalSeconds) {
        this.clearSchedulerIntervalSeconds = clearSchedulerIntervalSeconds;
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
