package com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity;

import java.util.List;

@SuppressWarnings("unused")
public class UserEntity {
    private Long id;
    private String login;
    private String password;
    private Long telegramId;
    private String telegramUsername;
    private List<PeerEntity> peers;

    public UserEntity() {
    }

    public UserEntity(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public String getTelegramUsername() {
        return telegramUsername;
    }

    public void setTelegramUsername(String telegramUsername) {
        this.telegramUsername = telegramUsername;
    }

    public List<PeerEntity> getPeers() {
        return peers;
    }

    public void setPeers(List<PeerEntity> peers) {
        this.peers = peers;
    }

    @Override
    public String toString() {
        return  "id=" + id +
                "\nlogin='" + login + '\'' +
                "\ntelegramId=" + telegramId +
                "\ntelegramUsername='" + telegramUsername + '\''
                ;
    }
}
