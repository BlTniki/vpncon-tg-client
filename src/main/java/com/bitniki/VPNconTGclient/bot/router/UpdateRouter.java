package com.bitniki.VPNconTGclient.bot.router;

import com.bitniki.VPNconTGclient.bot.dialogueTree.Tree;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.MailEntity;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.UserEntity;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.exception.UpdateRouterException;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class handle Update from bot,
 * routing chat through Dialogue branches
 * and response to the chat
 */
public class UpdateRouter {
    private final HashMap<Long, Tree> chatTreeMap;
    private final HashMap<Long, TreeKiller> treeKillerMap;
    private final RequestService requestService;

    private final Pattern chatIdPattern = Pattern.compile("id=\\s(\\d+)");

    public UpdateRouter(RequestService requestService) {
        this.chatTreeMap = new HashMap<>();
        this.treeKillerMap = new HashMap<>();
        this.requestService = requestService;
    }

    public List<Response<?>> handle(Update update) throws UpdateRouterException {
        if(update.hasPreCheckoutQuery()) {
            PreCheckoutQuery preCheckoutQuery = update.getPreCheckoutQuery();
            return handleQuery(preCheckoutQuery);
        }
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

    public List<Response<?>> handleQuery(PreCheckoutQuery preCheckoutQuery) {
        try {
            //Load user
            Long telegramId = preCheckoutQuery.getFrom().getId();
            UserEntity user = requestService.getUserByTelegramId(telegramId);

            //Get sub id from payload
            String payload = preCheckoutQuery.getInvoicePayload();
            Matcher matcher = chatIdPattern.matcher(payload);
            String subId;
            if(matcher.find())
                subId = matcher.group(1);
            else
                throw new Exception();

            //Add sub to user
            user = requestService.addSubToUser(subId, user.getId().toString());
            //Save Cheque
            MailEntity mail = new MailEntity(
                    user,
                    "User " + user.getLogin() + "Bayed sub with id " + subId + "\n at" + LocalDateTime.now()
                    );
            requestService.saveCheque(mail);

            //Answer
            List<Response<?>> responses = new ArrayList<>();
            AnswerPreCheckoutQuery answerPreCheckoutQuery = new AnswerPreCheckoutQuery(preCheckoutQuery.getId(), true);
            responses.add(new Response<>(ResponseType.SendAnswerToInvoice, answerPreCheckoutQuery));
            SendMessage sendMessage = new SendMessage(
                    telegramId.toString(),
                    "Success! You subscribed until " + user.getSubscriptionExpirationDay()
            );
            responses.add(new Response<>(ResponseType.SendText, sendMessage));
            return responses;
        } catch (Exception e) {
            List<Response<?>> responses = new ArrayList<>();
            AnswerPreCheckoutQuery answerPreCheckoutQuery = new AnswerPreCheckoutQuery(
                    preCheckoutQuery.getId(),
                    false,
                    "Internal error"
            );
            responses.add(new Response<>(ResponseType.SendAnswerToInvoice, answerPreCheckoutQuery));
            return responses;
        }
    }
}