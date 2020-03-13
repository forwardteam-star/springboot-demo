package com.darcytech.demo.mysql.node.enumerations;

import com.darcytech.demo.mysql.common.IntEnum;

public enum TradeStatus implements IntEnum {
    WAIT_BUYER_PAY(1), WAIT_SELLER_SEND_GOODS(2);

    final int value;

    TradeStatus(int value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return value;
    }

}
