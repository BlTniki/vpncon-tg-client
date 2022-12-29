package com.bitniki.VPNconTGclient.bot.response;

public class Response<T> {
    private ResponseType responseType;

    private T data;



    public Response(ResponseType responseType, T data) {
        this.responseType = responseType;
        this.data = data;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
