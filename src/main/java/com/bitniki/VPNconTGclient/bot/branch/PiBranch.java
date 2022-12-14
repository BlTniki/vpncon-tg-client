package com.bitniki.VPNconTGclient.bot.branch;

import com.bitniki.VPNconTGclient.bot.Response;
import com.bitniki.VPNconTGclient.bot.ResponseType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class PiBranch extends Branch{
    public PiBranch(Branch prevBranch) {
        super(prevBranch);
    }

    @Override
    public Response handle(Update update) {
        Message message = update.getMessage();
        Response response = new Response(message.getChatId());
        response.getResponses().put(ResponseType.SendMessage, "пидор");
        return response;
    }
}
