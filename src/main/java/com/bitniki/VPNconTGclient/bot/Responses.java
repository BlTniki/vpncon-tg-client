package com.bitniki.VPNconTGclient.bot;

import com.bitniki.VPNconTGclient.bot.branch.Branch;

import java.util.List;

/**
 * This class handle all messages, link to new branch
 * and chat id
 */
public class Responses {
    private Long chatId;
    private Branch nextBranch;

    private List<Response> responseList;

    public Responses() {
    }

    public Responses(Long chatId) {
        this.chatId = chatId;
    }

    public Responses(Long chatId, Branch nextBranch, List<Response> messageEntities) {
        this.chatId = chatId;
        this.nextBranch = nextBranch;
        this.responseList = messageEntities;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Branch getNextBranch() {
        return nextBranch;
    }

    public void setNextBranch(Branch nextBranch) {
        this.nextBranch = nextBranch;
    }

    public List<Response> getResponseList() {
        return responseList;
    }

    public void setResponseList(List<Response> messageEntities) {
        this.responseList = messageEntities;
    }
}
