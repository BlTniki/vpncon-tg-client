package com.bitniki.VPNconTGclient.bot.router;

import com.bitniki.VPNconTGclient.bot.dialogueTree.Tree;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.exception.UpdateRouterException;
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
    private final HashMap<Long, Tree> chatTreeMap;
    private final HashMap<Long, TreeKiller> treeKillerMap;
    private final RequestService requestService;

    public UpdateRouter(RequestService requestService) {
        this.chatTreeMap = new HashMap<>();
        this.treeKillerMap = new HashMap<>();
        this.requestService = requestService;
    }

    public List<Response<?>> handle(Update update) throws UpdateRouterException {
        if(!update.hasMessage())
            throw new UpdateRouterException("This is not a message!");

        Tree tree;
        if(chatTreeMap.containsKey(update.getMessage().getChatId())) {
            //get dialogue branch by chatId
            tree = chatTreeMap.get(update.getMessage().getChatId());
            //Refresh treeKiller task
            treeKillerMap.get(update.getMessage().getChatId()).refreshKillTime();
        } else {
            //if in HashMap no tree with this chatIp
            //Create tree
            tree = new Tree(requestService);
            chatTreeMap.put(update.getMessage().getChatId(), tree);
            //Set Task to kill chat
            treeKillerMap.put(update.getMessage().getChatId(),
                    new TreeKiller(update.getMessage().getChatId(), this));
        }

        return tree.handle(update);
    }

    /**
     * Deletes chat from chatBranchMap and task from treeKillerMap.
     * Uses by ScheduledExecutorService to remove afk chats.
     * @param chatId chat id
     */
    public void deleteChatFromMaps(Long chatId) {
        chatTreeMap.remove(chatId);
        treeKillerMap.remove(chatId);
    }
}