package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.FAQ;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.request.RequestService.RequestServiceFactory;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class GeneralFaqBranch extends Branch {
    private enum BranchState {
        InitState(),
        WaitingForButtonChoose()
    }
    private BranchState branchState = BranchState.InitState;

    private final String generalText = """
            Наш сервис предлагает *VPN*-услуги. Мы даем вам возможность получить специальные файлы конфигурации (*Конфигов*), которые вы можете использовать в приложении *Wireguard*. Это приложение доступно для скачивания на разных платформах, таких как Windows, iOS и Android. Чтобы получить доступ к этим файлам, вам нужно оформить *подписку*.
                        
            По всем вопросам можно обратиться ко мне: @BITniki
            """;

    private final String aboutSubsButton = "Как оформить подписку?";
    private final String aboutCreatePeerButton = "Как создать конфиг?";
    private final String aboutWireguardButton = "Как скачать Wireguard открыть в нём конфиг?";

    public GeneralFaqBranch(Branch prevBranch, RequestServiceFactory requestService) {
        super(prevBranch, requestService);
    }

    @Nullable
    @Override
    protected List<Response<?>> makeResponses(Update update) throws BranchCriticalException {
        //Get message from update
        Message message = update.getMessage();

        if(branchState.equals(BranchState.InitState)) {
            return provideFaqButtons(message);
        }
        if(branchState.equals(BranchState.WaitingForButtonChoose)) {
            String text = getTextFrom(message);
            if(text.equals(aboutSubsButton)) {
                return routeToAboutSubsBranch();
            }
            if(text.equals(aboutCreatePeerButton)) {
                return routeToAboutCreatePeerBranch();
            }
            if(text.equals(aboutWireguardButton)) {
                return routeToAboutWireguardBranch();
            }
        }

        return null;
    }

    private List<Response<?>> provideFaqButtons(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), generalText);
        //Set Markdown
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        //set nav buttons
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton(1,
                        aboutSubsButton,
                        aboutCreatePeerButton,
                        aboutWireguardButton
                )
        );
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        branchState = BranchState.WaitingForButtonChoose;
        return responses;
    }

    private List<Response<?>> routeToAboutSubsBranch() {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Change branch
        setNextBranch(new AboutSubsBranch(this, requestService));

        return responses;
    }
    private List<Response<?>> routeToAboutCreatePeerBranch() {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Change branch
        setNextBranch(new AboutCreatePeerBranch(this, requestService));

        return responses;
    }

    private List<Response<?>> routeToAboutWireguardBranch() {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Change branch
        setNextBranch(new AboutWireguardBranch(this, requestService));

        return responses;
    }
}
