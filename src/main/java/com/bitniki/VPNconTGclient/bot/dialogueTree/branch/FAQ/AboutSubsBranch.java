package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.FAQ;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.io.File;
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
            В главном меню есть раздел "*Оплатить*". Там вы можете выбрать подходящую подписку, в зависимости от цены и количества доступных конфигов. При нажатии на "Оплатить", вам откроется страница для оплаты. То есть сколько конфигов вы можете создать.\s
            При нажатии на кнопку оплатить, что находится под подпиской, вам откроется окно оплаты.
            
            """;

    private final String aboutSubsText2 = """
            После выбора подписки, вам будет предложено произвести оплату через платежный провайдер *ЮKаssа*. Пожалуйста, *имейте в виду*, что я не имею доступа к вашим платежным данным, вся обработка платежей происходит через Телеграм и платежный провайдер.
                        
            Если вы хотите *продлить* подписку на большее количество времени, чем предлагается в одной подписке, вы можете купить ту же самую подписку несколько раз.
                        
            *Важно отметить*: Если у вас уже есть подписка, и вы оплатите другую. То предыдущая подписка сгорит полностью. Если у вас есть желание поменять подписку, свяжитесь со мной: @BITniki
            """;

    private final String aboutSubsPhotoPath = "src/main/resources/static/image/aboutSubs.png";

    public AboutSubsBranch(Branch prevBranch, RequestService requestService) {
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
        sendPhoto.setPhoto(new InputFile( new File(aboutSubsPhotoPath)));
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