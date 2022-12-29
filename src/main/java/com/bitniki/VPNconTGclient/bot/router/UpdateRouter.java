package com.bitniki.VPNconTGclient.bot.router;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.dialogueTree.Tree;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.exception.UpdateRouterException;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;

/**
 * This class handle Update from bot,
 * routing chat through Dialogue branches
 * and response to the chat
 */
public class UpdateRouter {
    private final HashMap<Long, Branch> chatBranchMap;
    private final RequestService requestService;
    public UpdateRouter(RequestService requestService) {
        this.chatBranchMap = new HashMap<Long, Branch>();
        this.requestService = requestService;
    }

    public List<Response<?>> handle(Update update) throws UpdateRouterException {
        if(!update.hasMessage())
            throw new UpdateRouterException("This is not a message!");

        Branch branch;
        if(chatBranchMap.containsKey(update.getMessage().getChatId())) {
            //get dialogue branch by chatId
            branch = chatBranchMap.get(update.getMessage().getChatId());
        } else {
            //if in HashMap no branch with this chatIp
            //Create
            branch = new InitBranch(null, requestService);
            chatBranchMap.put(update.getMessage().getChatId(), branch);
        }
        Responses responses = branch.handle(update);
        //if Dialogue want to change branch, change
        if(responses.getNextBranch() != null) {
            chatBranchMap.put(responses.getChatId(), responses.getNextBranch());
            //do init response
            responses.getResponseList().addAll(
                    this.handle(update).getResponseList()
            );
        }
        return responses;
    }
}
