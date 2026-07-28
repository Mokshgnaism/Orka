package com.Orka.util;

import com.Orka.apiContract.generated.ProtoHttpResponse;

public class HttpUtil {
    public static ProtoHttpResponse getProtoHttpResponse(String error, int statusCode){
        return ProtoHttpResponse.newBuilder().setError(error).setStatusCode(statusCode).build();
    }
}
