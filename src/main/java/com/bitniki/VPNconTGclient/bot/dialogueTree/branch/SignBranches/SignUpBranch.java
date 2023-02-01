package com.bitniki.VPNconTGclient.bot.dialogueTree.branch.SignBranches;

import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.AuthBranch;
import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.Branch;
import com.bitniki.VPNconTGclient.bot.dialogueTree.branch.InitBranch;
import com.bitniki.VPNconTGclient.bot.exception.BranchCriticalException;
import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.UserEntity;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import com.bitniki.VPNconTGclient.bot.exception.BranchBadUpdateProvidedException;
import com.bitniki.VPNconTGclient.bot.exception.requestHandlerException.RequestService5xxException;
import com.bitniki.VPNconTGclient.bot.exception.validationFailedException.UserValidationFailedException;
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
    private final String loginText =    "Придумай себе логин. Он должен быть длиной от 1 до 20 и может состоять из:\n" +
                                        "Строчного или прописного латинского алфавита\n" +
                                        "Цифр\n" +
                                        "Знаков: - _ ."
                                        ;
    private final String passwordText = "Так, а теперь пароль: Он должен быть длиной от 3 символов и должен состоять из:\n" +
                                        "Строчного и прописного латинского алфавита\n" +
                                        "Цифр\n" +
                                        "А также можно добавить другие символы"
                                        ;
    private final String endText = "Создал тебе аккаунт! вот он:\n";

    public SignUpBranch(Branch prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
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

        //Make Response
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), loginText);
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton());
        responses.add(new Response<>(ResponseType.SendText, sendMessage));
        this.userEntity = new UserEntity();

        //Change Branch state
        branchState = BranchState.WaitForLogin;
        return responses;
    }

    private List<Response<?>> askPassword(Message message) throws BranchBadUpdateProvidedException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Make Response
        userEntity.setLogin(getTextFrom(message));
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), passwordText);
        sendMessage.setReplyMarkup(makeKeyboardMarkupWithMainButton());
        responses.add(new Response<>(ResponseType.SendText, sendMessage));

        //Change Branch state
        branchState = BranchState.WaitForPassword;
        return responses;
    }

    private List<Response<?>> createUser(Message message)
            throws BranchCriticalException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //end build entity
        userEntity.setPassword(getTextFrom(message));
        userEntity.setTelegramId(message.getFrom().getId());
        userEntity.setTelegramUsername(message.getFrom().getFirstName());

        //try to create user on server
        try {
            this.userEntity = requestService.createUserOnServer(this.userEntity);
        } catch (UserValidationFailedException e) {
            throw new UserValidationFailedException("Похоже ты накосячил, у тебя:\n" +
                                                    e.getMessage() +
                                                    "\nПопробуй ещё раз");
        } catch (RequestService5xxException e) {
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
