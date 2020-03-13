package com.darcytech.demo.mysql.node.enumerations;

import com.darcytech.demo.mysql.common.IntEnum;

public enum TradeType implements IntEnum {
    fixed(1);

    final int value;

    TradeType(int value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return value;
    }

}
