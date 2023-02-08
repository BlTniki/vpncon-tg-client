package com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity;

import java.time.LocalDate;
import java.util.List;

@SuppressWarnings("unused")
public class UserEntity {
    private Long id;
    private String login;
    private String password;
    private String role;
    private Long telegramId;
    private String telegramUsername;
    private List<PeerEntity> peers;
    private LocalDate subscriptionExpirationDay;
    private SubscriptionEntity subscription;

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDate getSubscriptionExpirationDay() {
        return subscriptionExpirationDay;
    }

    public void setSubscriptionExpirationDay(LocalDate subscriptionExpirationDay) {
        this.subscriptionExpirationDay = subscriptionExpirationDay;
    }

    public SubscriptionEntity getSubscription() {
        return subscription;
    }

    public void setSubscription(SubscriptionEntity subscription) {
        this.subscription = subscription;
    }

    @Override
    public String toString() {
        return  "id=" + id +
                "\nlogin= " + login +
                "\ntelegramId= " + telegramId +
                "\ntelegramUsername= " + telegramUsername;
    }
}
