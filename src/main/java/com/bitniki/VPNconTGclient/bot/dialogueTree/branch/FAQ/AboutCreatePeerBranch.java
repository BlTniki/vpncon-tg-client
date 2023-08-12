package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.FAQ;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.RequestServiceFactory;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class AboutCreatePeerBranch extends Branch {
    private enum BranchState {
        InitState(),
        WaitingForButtonChoose()
    }
    private BranchState branchState = BranchState.InitState;

    private final String aboutCreatePeerText = """
            После оплаты подписки у вас появится доступ к созданию конфигов. Для этого следует нажать: *Настройка VPN* -> *Создать конфиг* -> ??? -> *Готово*. Бот отправит вам файл, который следует открыть в *Wireguard*. В соседнем разделе можно ещё раз получить конфиг или удалить его.
            """;
    private final String aboutCreatePeerPhotoPath = "static/image/aboutCreatePeer.png";

    public AboutCreatePeerBranch(Branch prevBranch, RequestServiceFactory requestService) {
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
        sendPhoto.setCaption(aboutCreatePeerText);
        //Set Markdown
        sendPhoto.setParseMode(ParseMode.MARKDOWN);
        //set nav buttons
        sendPhoto.setReplyMarkup(makeKeyboardMarkupWithMainButton());
        //load file
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream photoStream = classLoader.getResourceAsStream(aboutCreatePeerPhotoPath);
        sendPhoto.setPhoto(new InputFile(photoStream, "aboutCreatePeer.png"));
        responses.add(new Response<>(ResponseType.SendPhoto, sendPhoto));

        branchState = BranchState.WaitingForButtonChoose;
        return responses;
    }
}
