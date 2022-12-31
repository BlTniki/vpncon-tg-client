package com.bitniki.VPNconTGclient.bot.dialogueTree.branch;

import com.bitniki.VPNconTGclient.bot.requestHandler.RequestService;
import com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity.UserEntity;
import com.bitniki.VPNconTGclient.bot.response.Response;
import com.bitniki.VPNconTGclient.bot.response.ResponseType;
import com.bitniki.VPNconTGclient.exception.BranchBadUpdateProvidedException;
import com.bitniki.VPNconTGclient.exception.RequestService5xxException;
import com.bitniki.VPNconTGclient.exception.RequestServiceException;
import com.bitniki.VPNconTGclient.exception.notFoundException.UserNotFoundException;
import com.bitniki.VPNconTGclient.exception.validationFailedException.UserValidationFailedException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;

import java.util.ArrayList;
import java.util.List;

public class SignUpBranch extends BranchWithUser{
    private final String loginText =    """
                                            Придумай себе логин. Он должен быть длиной от 1 до 20 и может состоять из:
                                            Строчного или прописного латинского алфавита
                                            Цифр
                                            Знаков: - _ .
                                        """;
    private final String passwordText = """
                                            Так, а теперь пароль: Он должен быть длиной от 3 символов и должен состоять из:
                                            Строчного и прописного латинского алфавита
                                            Цифр
                                            А также можно добавить другие символы
                                        """;
    private final String endText = "Создал тебе аккаунт! вот он:\n";
    private final String signUpButtonText = "Регистрация";

    public SignUpBranch(RequestService requestService) {
        super(requestService);
    }

    public SignUpBranch(Branch prevBranch, RequestService requestService) {
        super(prevBranch, requestService);
    }

    public SignUpBranch(BranchWithUser branch, RequestService requestService) {
        super(branch, requestService);
    }

    @Override
    public List<Response<?>> handle(Update update)
            throws RequestServiceException, BranchBadUpdateProvidedException {
        //Get message from update
        Message message = update.getMessage();

        //ask login branch state
        if(message.getText().equals(signUpButtonText)) {
            return askLogin(message);
        }
        //ask password branch state
        if(message.getReplyToMessage() != null && message.getReplyToMessage().getText().equals(loginText)) {
            return askPassword(message);
        }
        //associateUser branch state
        if(message.getReplyToMessage() != null && message.getReplyToMessage().getText().equals(passwordText)) {
            return createUser(message);
        }
        //if we got here send error
        throw new BranchBadUpdateProvidedException(
                "Что-то я не смог тебя понять. Давай кинем тебя в начало"
        );
    }

    private List<Response<?>> askLogin(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Make Response
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), loginText);
        sendMessage.setReplyMarkup(new ForceReplyKeyboard(true));
        responses.add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
        this.userEntity = new UserEntity();
        return responses;
    }

    private List<Response<?>> askPassword(Message message) {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //Make Response
        userEntity.setLogin(message.getText());
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), passwordText);
        sendMessage.setReplyMarkup(new ForceReplyKeyboard(true));
        responses.add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
        return responses;
    }

    private List<Response<?>> createUser(Message message)
            throws UserValidationFailedException, RequestService5xxException {
        //Init Responses
        List<Response<?>> responses = new ArrayList<>();

        //end build entity
        userEntity.setTelegramId(message.getFrom().getId());
        userEntity.setTelegramUsername(message.getFrom().getUserName());

        //try to create user on server
        try {
            this.userEntity = requestService.createUserOnServer(this.userEntity);
        } catch (UserValidationFailedException e) {
            throw new UserValidationFailedException("Похоже ты накосячил, у тебя:\n" +
                                                    e.getMessage() +
                                                    "Попробуй ещё раз");
        }
        //Make Response
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                endText + "\n" + userEntity);
        responses.add(new Response<SendMessage>(ResponseType.SendText, sendMessage));
        //route to InitBranch
        this.setNextBranch(new InitBranch(this, requestService));
        return responses;
    }
}
