package com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity;

@SuppressWarnings("unused")
public class MailEntity {
    private  Long id;
    private UserEntity user;
    private Boolean forTelegram=false;
    private Boolean isChecked=false;
    private String payload;

    public MailEntity() {
    }

    public MailEntity(UserEntity user, String payload) {
        this.user = user;
        this.payload = payload;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Boolean getForTelegram() {
        return forTelegram;
    }

    public void setForTelegram(Boolean forTelegram) {
        this.forTelegram = forTelegram;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
