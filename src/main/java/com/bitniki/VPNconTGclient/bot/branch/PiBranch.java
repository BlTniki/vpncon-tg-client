package com.bitniki.VPNconTGclient.bot.branch;

import com.bitniki.VPNconTGclient.bot.Response;
import com.bitniki.VPNconTGclient.bot.ResponseType;
import com.bitniki.VPNconTGclient.bot.Responses;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;

public class PiBranch extends Branch{
    public PiBranch(Branch prevBranch) {
        super(prevBranch);
    }

    @Override
    public Responses handle(Update update) {
        Message message = update.getMessage();
        Responses responses = new Responses(message.getChatId());
        responses.setResponseList(new ArrayList<>());
        responses.getResponseList().add(new Response<String>(ResponseType.SendText, "Пидор"));
        return responses;
    }
}
