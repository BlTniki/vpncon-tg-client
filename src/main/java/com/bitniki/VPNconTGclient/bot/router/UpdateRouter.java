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

        Long chatId = update.getMessage().getChatId();
        Tree tree = chatTreeMap.computeIfAbsent(chatId,
                key -> new Tree(requestService));
        treeKillerMap.computeIfPresent(chatId,
                (key, treeKiller) -> {
                    treeKiller.refreshKillTime();
                    return treeKiller;
                });

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