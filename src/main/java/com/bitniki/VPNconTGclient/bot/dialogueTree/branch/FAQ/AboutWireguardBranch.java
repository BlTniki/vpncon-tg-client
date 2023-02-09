package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.FAQ;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class AboutWireguardBranch extends Branch {
    private enum BranchState {
        InitState(),
        WaitingForButtonChoose()
    }
    private BranchState branchState = BranchState.InitState;

    private final String aboutWireguardText = """
            Скачать *Wireguard* можно как с официального сайта (для ПК), так из AppStore или Google Play (Для смартфонов).
            После установки и запуска откроется главное окно, в котором следует найти кнопку *Добавить туннель* или *Импорт из файла*. Нажать на неё и выбрать конфиг.
            """;
    private final String aboutWireguardPhotoPath = "src/main/resources/static/image/aboutWireguard.png";

    public AboutWireguardBranch(Branch prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
    }

    @Nullable
    @Override
    protected List<Response<?>> makeResponses(Update update) throws BranchCriticalException {
        //Get message from update
        Message message = update.getMessage();

        if(branchState.equals(BranchState.InitState)) {
            return provideAnswer(message);
        }

        return null;
    }

    private List<Response<?>> provideAnswer(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Send Photo with caption
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(message.getChatId());
        sendPhoto.setPhoto(new InputFile( new File(aboutWireguardPhotoPath)));
        sendPhoto.setCaption(aboutWireguardText);
        //Set Markdown
        sendPhoto.setParseMode(ParseMode.MARKDOWN);
        //set nav buttons
        sendPhoto.setReplyMarkup(makeKeyboardMarkupWithMainButton());
        responses.add(new Response<>(ResponseType.SendPhoto, sendPhoto));

        branchState = BranchState.WaitingForButtonChoose;
        return responses;
    }
}
