package com.bitniki.VPNconTGclient.bot.response;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;

import java.util.List;

/**
 * This class handle all messages, link to new branch
 * and chat id
 */
public class Responses {


    private List<Response<?>> responseList;

    public Responses() {
    }

    public Responses(Long chatId) {

    }

    public Responses(Long chatId, Branch nextBranch, List<Response<?>> messageEntities) {
        this.responseList = messageEntities;
    }

    public List<Response<?>> getResponseList() {
        return responseList;
    }

    public void setResponseList(List<Response<?>> messageEntities) {
        this.responseList = messageEntities;
    }
}
