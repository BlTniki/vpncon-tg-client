package com.bitniki.VPNconTGclient.bot;

import java.util.HashMap;

public class Response {
    public Long chatId;

    private HashMap<ResponseType, Object> responses;

    public Response(Long chatId) {
        this.chatId = chatId;
        this.responses = new HashMap<>();
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public HashMap<ResponseType, Object> getResponses() {
        return responses;
    }

    public void setResponses(HashMap<ResponseType, Object> responses) {
        this.responses = responses;
    }

}
