package com.bitniki.VPNconTGclient.bot.branch;

import com.bitniki.VPNconTGclient.bot.Response;
import com.bitniki.VPNconTGclient.bot.ResponseType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class InitBranch extends Branch{
    public InitBranch(Branch prevBranch) {
        super(prevBranch);
    }

    @Override
    public Response handle(Update update) {
        Message message = update.getMessage();
        Response response = new Response(message.getChatId());
        if(message.isCommand() && message.getText().equals("/start")) {
            response.getResponses().put(ResponseType.SendMessage, "hi");
        } else {
            response.getResponses().put(ResponseType.SendMessage, message.getText());
        }
        response.getResponses().put(ResponseType.ChangeBranch, new PiBranch(this));
        return response;
    }
}
