package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.SignBranches;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.AuthBranch;
import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.InitBranch;
import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.UserEntity;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.UserValidatorRegex;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.impl.UserForRequest;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.RequestServiceFactory;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.ModelNotFoundException;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.ModelValidationFailedException;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"TextBlockMigration", "FieldCanBeLocal", "unused"})
public class SignUpBranch extends AuthBranch {
    private enum BranchState {
        InitState(),
        WaitForLogin(),
        WaitForPassword()
    }
    private BranchState branchState = BranchState.InitState;
    private UserEntity userEntity;
    private UserForRequest credentials;
    private final UserValidatorRegex validator;
    private final String loginText = "Придумай себе логин. Длиной до 20 символов и может состоять из:\n" +
            "- Заглавного и строчного латинского алфавита\n" +
            "- Цифр\n" +
            "- Знаков: - \\_ .";
    private final String passwordText = "Так, а теперь пароль. Длиной от 3 символов и *должен* состоять из:\n" +
                                        "- Строчного и прописного латинского алфавита\n" +
                                        "- Цифр\n" +
                                        "А также можно добавить другие символы"
                                        ;
    private final String endText = "Создал тебе аккаунт! вот он:\n";

    public SignUpBranch(Branch prevBranch, RequestServiceFactory requestService) {
        super(prevBranch, requestService);
        this.validator = requestService.USER_REQUEST_SERVICE.getValidatorFields();
    }

    @Override
    public List<Response<?>> makeResponses(Update update)
            throws BranchCriticalException {
        //Get message from update
        Message message = update.getMessage();

        //ask login branch state
        if(branchState.equals(BranchState.InitState)) {
            return askLogin(message);
        }
        //ask password branch state
        if(branchState.equals(BranchState.WaitForLogin)) {
            return askPassword(message);
        }
        //associateUser branch state
        if(branchState.equals(BranchState.WaitForPassword)) {
            return createUser(message);
        }
        //If we got here return null
        return null;
    }

    private List<Response<?>> askLogin(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        // init entities
        this.userEntity = new UserEntity();
        this.credentials = new UserForRequest();

        //Make Response
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), loginText);
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton());
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        //Change Branch state
        branchState = BranchState.WaitForLogin;
        return responses;
    }

    private List<Response<?>> askPassword(Message message) throws BranchBadUpdateProvidedException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        // valid and set login
        String login = getTextFrom(message);
        if (validator.isLoginNotValid(login)) {
            //Make error Response
            SendMessage sendMessage = new SendMessage(message.getChatId().toString(), "Неподходящий логин. Давай придумаем другой");
            sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton());
            responses.add(new Response<>(ResponseType.SendText, sendMessage));
        } else if (requestService.USER_REQUEST_SERVICE.isLoginIsNotUnique(login)) {
            //Make error Response
            SendMessage sendMessage = new SendMessage(message.getChatId().toString(), "Такой логин уже существует. Давай придумаем другой");
            sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton());
            responses.add(new Response<>(ResponseType.SendText, sendMessage));
        } else {
            this.credentials.setLogin(login);

            //Make Response
            SendMessage sendMessage = new SendMessage(message.getChatId().toString(), passwordText);
            sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton());
            responses.add(new Response<>(ResponseType.SendText, sendMessage));
            //Change Branch state
            this.branchState = BranchState.WaitForPassword;
        }

        return responses;
    }

    private List<Response<?>> createUser(Message message)
            throws BranchCriticalException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        // valid and set password
        String password = getTextFrom(message);
        if (validator.isPasswordNotValid(password)) {
            //Make error Response
            SendMessage sendMessage = new SendMessage(message.getChatId().toString(), "Неподходящий пароль. Давай придумаем другой");
            sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton());
            responses.add(new Response<>(ResponseType.SendText, sendMessage));
            return responses;
        }

        //end build credentials
        this.credentials.setPassword(password);
        this.credentials.setTelegramId(message.getFrom().getId());
        this.credentials.setTelegramNickname(message.getFrom().getUserName());


        //try to create user and associate on server
        try {
            // create
            requestService.USER_REQUEST_SERVICE.createUserOnServer(credentials);
            // associate
            this.userEntity = requestService.USER_REQUEST_SERVICE.associateTelegramIdWithUser(credentials);
        } catch (ModelValidationFailedException | ModelNotFoundException e) {
            // we don't expect any error here, so
            throw new BranchCriticalException(e.getMessage());
        }

        //Make Response
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                endText + "\n" + userEntity);
        responses.add(new Response<>(ResponseType.SendText, sendMessage));
        //route to InitBranch
        this.setNextBranch(new InitBranch(this, requestService));
        return responses;
    }
}
