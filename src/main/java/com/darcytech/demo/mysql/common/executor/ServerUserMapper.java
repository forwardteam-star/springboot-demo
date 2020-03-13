package com.darcytech.demo.mysql.common.executor;

import javax.annotation.Nullable;

public interface ServerUserMapper {

    @Nullable
    Integer resolveServerId(Long userId);

}
