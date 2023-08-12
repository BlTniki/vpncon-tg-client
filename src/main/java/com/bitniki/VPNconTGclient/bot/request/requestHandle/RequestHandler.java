package com.bitniki.VPNconTGclient.bot.request.requestHandle;

import com.bitniki.VPNconTGclient.bot.request.Model.Model;
import com.bitniki.VPNconTGclient.bot.request.ModelForRequest.ModelForRequest;
import com.bitniki.VPNconTGclient.bot.request.exception.requestHandler.RequestHandlerException;


/**
 * Обработчик запросов до сервера.
 */
public interface RequestHandler {
    /**
     * Аутентификация на сервере и установка JWT токена в хэдеры запросов.
     */
    void SignInAndMakeHeaders() throws RequestHandlerException;

    /**
     * GET запрос на сервер.
     * @param endpoint эндпоинт сервера, куда следует совершить запрос, с необходимыми параметрами.
     * @param responseBodyClass Класс модели ответа сервера.
     * @return Ответ сервера в виде данной модели ответа
     * @param <T> Класс модели ответа сервера.
     * @throws RequestHandlerException
     * RequestHandler400Exception Если запрос не прошёл валидацию.
     * RequestHandler404Exception Если запрашиваемая сущность не найдена.
     * RequestHandler5xxException Если сервер вернул внутреннюю ошибку.
     * RequestHandlerException В случае непредвиденной ошибки.
     */
    <T> T GET(String endpoint, Class<T> responseBodyClass) throws RequestHandlerException;

    /**
     * DELETE запрос на сервер.
     * @param endpoint эндпоинт сервера, куда следует совершить запрос, с необходимыми параметрами.
     * @param responseBodyClass Класс модели ответа сервера.
     * @return Ответ сервера в виде данной модели ответа
     * @param <T> Класс модели ответа сервера.
     * @throws RequestHandlerException
     * RequestHandler400Exception Если запрос не прошёл валидацию.
     * RequestHandler404Exception Если запрашиваемая сущность не найдена.
     * RequestHandler5xxException Если сервер вернул внутреннюю ошибку.
     * RequestHandlerException В случае непредвиденной ошибки.
     */
    <T extends Model> T DELETE(String endpoint, Class<T> responseBodyClass) throws RequestHandlerException;

    /**
     * POST запрос на сервер.
     * @param endpoint эндпоинт сервера, куда следует совершить запрос, с необходимыми параметрами.
     * @param requestBody Тело запроса на сервер.
     * @param responseBodyClass Класс модели ответа сервера.
     * @return Ответ сервера в виде данной модели ответа
     * @param <T> Класс модели ответа сервера.
     * @param <K> Класс модели тела запроса.
     * @throws RequestHandlerException
     * RequestHandler400Exception Если запрос не прошёл валидацию.
     * RequestHandler404Exception Если запрашиваемая сущность не найдена.
     * RequestHandler5xxException Если сервер вернул внутреннюю ошибку.
     * RequestHandlerException В случае непредвиденной ошибки.
     */
    <K extends ModelForRequest, T> T POST(String endpoint, K requestBody, Class<T> responseBodyClass) throws RequestHandlerException;

    /**
     * PUT запрос на сервер.
     * @param endpoint эндпоинт сервера, куда следует совершить запрос, с необходимыми параметрами.
     * @param requestBody Тело запроса на сервер.
     * @param responseBodyClass Класс модели ответа сервера.
     * @return Ответ сервера в виде данной модели ответа
     * @param <T> Класс модели ответа сервера.
     * @param <K> Класс модели тела запроса.
     * @throws RequestHandlerException
     * RequestHandler400Exception Если запрос не прошёл валидацию.
     * RequestHandler404Exception Если запрашиваемая сущность не найдена.
     * RequestHandler5xxException Если сервер вернул внутреннюю ошибку.
     * RequestHandlerException В случае непредвиденной ошибки.
     */
    <K extends ModelForRequest, T extends Model> T PUT(String endpoint, K requestBody, Class<T> responseBodyClass) throws RequestHandlerException;
}
