package com.darcytech.demo.mysql.common;

import javax.annotation.Nullable;

public class ThreadLocalDataSourceSelector implements DataSourceSelector {

    private static final ThreadLocal<Integer> datasourceKey = new ThreadLocal<>();

    public void set(Integer dataSourceKey) {
        datasourceKey.set(dataSourceKey);
    }

    public void clear() {
        datasourceKey.remove();
    }

    @Override
    @Nullable
    public Integer get() {
        return datasourceKey.get();
    }

}
