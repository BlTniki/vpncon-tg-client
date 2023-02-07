package com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity;

@SuppressWarnings("unused")
public class SubscriptionEntity {
    private Long id;
    private String role;
    private Integer priceInRub = 0;
    private Integer peersAvailable = 0;
    private Integer days = 0;

    public SubscriptionEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getPriceInRub() {
        return priceInRub;
    }

    public void setPriceInRub(Integer priceInRub) {
        this.priceInRub = priceInRub;
    }

    public Integer getPeersAvailable() {
        return peersAvailable;
    }

    public void setPeersAvailable(Integer peersAvailable) {
        this.peersAvailable = peersAvailable;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public String describe() {
        return  "Цена в рублях: " + priceInRub +
                ".\nКол-во доступных конфигов: " + peersAvailable +
                ".\n Кол-во дней: " + days;
    }

    @Override
    public String toString() {
        return "SubscriptionEntity{" +
                "\nid= " + id +
                "\nrole= " + role +
                "\npriceInRub= " + priceInRub +
                "\npeersAvailable= " + peersAvailable +
                "\ndays= " + days +
                "\n}";
    }
}
