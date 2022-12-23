package com.bitniki.VPNconTGclient.bot;

import com.bitniki.VPNconTGclient.bot.branch.Branch;
import com.bitniki.VPNconTGclient.bot.branch.InitBranch;
import com.bitniki.VPNconTGclient.exception.UpdateRouterException;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;

/**
 * This class handle Update from bot,
 * routing chat through Dialogue branches
 * and response to the chat
 */
public class UpdateRouter {
    private final HashMap<Long, Branch> chatBranchMap;

    public UpdateRouter() {
        this.chatBranchMap = new HashMap();
    }

    public Responses handle(Update update) throws UpdateRouterException {
        if(!update.hasMessage())
            throw new UpdateRouterException("This is not a message!");
        //get dialogue branch by chatId
        Branch branch = chatBranchMap.get(update.getMessage().getChatId());
        //if in HashMap no branch with this chatIp
        //Create
        if(branch == null) {
            branch = new InitBranch(null);
            chatBranchMap.put(update.getMessage().getChatId(), branch);
        }
        Responses responses = branch.handle(update);
        //if Dialogue want to change branch, change
        if(responses.getNextBranch() != null) {
            chatBranchMap.put(responses.getChatId(), responses.getNextBranch());
        }
        return responses;
    }
}
