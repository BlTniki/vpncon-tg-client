package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.FAQ;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.RequestServiceFactory;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class AboutSubsBranch extends Branch {
    private enum BranchState {
        InitState(),
        WaitingForButtonChoose()
    }
    private BranchState branchState = BranchState.InitState;

    private final String aboutSubsText1 = """
            В главном меню есть раздел "*Оплатить*". Там вы можете выбрать подходящую подписку, в зависимости от цены и количества доступных конфигов. То есть сколько конфигов вы можете создать.\s
            При нажатии оплатить вам откроется окно оплаты.. При нажатии на "Оплата по *ссылке*", вам откроется страница для оплаты, где при нажатии "*Оплатить*" откроется форма ввода платёжных данных. После успешной оплаты вас переведёт обратно автоматически, либо можно нажать "*Вернутся в магазин*". Этап возвращения обратно **крайне важен** для успешной оплаты.
            """;

    private final String aboutSubsText2 = """
            В качестве платежного провайдера используется *P2P QIWI*. Пожалуйста, *имейте в виду*, что я не имею доступа к вашим платежным данным, вся обработка платежей происходит через платежного провайдера.
                        
            Если вы хотите *продлить* подписку на большее количество времени, чем предлагается в одной подписке, вы можете купить ту же самую подписку несколько раз.
                        
            *Важно отметить*: Если у вас уже есть подписка, и вы оплатите другую. То предыдущая подписка сгорит полностью. Если у вас есть желание поменять подписку, свяжитесь со мной: @BITniki
            """;

    private final String aboutSubsPhotoPath = "static/image/aboutSubs.png";

    public AboutSubsBranch(Branch prevBranch, RequestServiceFactory requestService) {
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
        sendPhoto.setCaption(aboutSubsText1);
        sendPhoto.setChatId(message.getChatId());
        sendPhoto.setParseMode(ParseMode.MARKDOWN);
        //load file
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream photoStream = classLoader.getResourceAsStream(aboutSubsPhotoPath);
        sendPhoto.setPhoto(new InputFile(photoStream, "aboutSubs.png"));
        responses.add(new Response<>(ResponseType.SendPhoto, sendPhoto));

        //Send second message
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), aboutSubsText2);
        //Set Markdown
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        //set nav buttons
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton());
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        branchState = BranchState.WaitingForButtonChoose;
        return responses;
    }
}
