package com.darcytech.demo.mysql.common;

import javax.annotation.Nullable;

public interface DataSourceSelector {

    /**
     *
     * @Return 数据源标识, 以供路由数据源选择对应的真实数据源
     *         如果当前线程没有设置数据源标识, 返回 null
     *
     */
    @Nullable Integer get();

}
