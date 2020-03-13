package com.darcytech.demo.web.common;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DecryptSerializer extends JsonSerializer<String> {

    //    private TaobaoSecretClient taobaoSecretClient;

    private final boolean phone;

    public DecryptSerializer(boolean phone) {
        this.phone = phone;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        //        Operator currentOperator = Operator.getCurrentOperator();
        //        Preconditions.checkArgument(currentOperator != null, "解密时当前用户必须存在");
        //        gen.writeString(taobaoSecretClient.decryptNick(currentOperator.getSessionKey(), value));
        String result = phone ? value + "phone" : value + "nick";
        gen.writeString(result);
    }

}

