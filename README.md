# vpncon-tg-client
Это клиент для [VPNconServer](https://github.com/BlTniki/vpnconserver).

## Initialization RU
Все команды выполняются в корне проекта.
### Базовая инициализация
Клонируете проект.
Создаёте `application.properties` в `src\main\resources`.
Пример `application.properties`:
```properties
server.port =   
  
tg.botToken = # Token from BotFather  
tg.botUsername = # Bot username in telegram  
  
VPNconServer.address = # Server address like http://0.0.0.0:0000  
VPNconServer.domain = # Server domain like http://example.com  
VPNconServer.login = telegramBot # Username for bot on server. dont change  
VPNconServer.password = # Username for bot on server.
```
Должен быть установлен __java__ и __gradle__.
Запускаете скрипт `gradlew` (`./gradlew` в Linux).

### Сборка Docker контейнера
Должен быть установлен __java__ и __gradle__.
Клонируете проект.
Скомпилируйте проект:
```bash
./gradlew build -x test
```
`-x test` используется для игнорирования тестов, так как они дадут ошибку из-за не настроенного подключения к базе данных и отсутствия `application.properties` (что нам и необязательно настраивать).
Далее собираем образ:
```bash
docker build -t vpnconbot .
```

Пример `docker compose` можно найти в репозитории [VPNcon](https://github.com/BlTniki/vpnconserver)
