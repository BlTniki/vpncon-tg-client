package com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity;

@SuppressWarnings("unused")
public class HostEntity {
    private Long id;
    private String name;
    private String ipadress;
    private String serverPublicKey;
    private String dns;

    public HostEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpadress() {
        return ipadress;
    }

    public void setIpadress(String ipadress) {
        this.ipadress = ipadress;
    }

    public String getServerPublicKey() {
        return serverPublicKey;
    }

    public void setServerPublicKey(String serverPublicKey) {
        this.serverPublicKey = serverPublicKey;
    }

    public String getDns() {
        return dns;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }

    @Override
    public String toString() {
        return "HostEntity{" +
                "\nid=" + id +
                "\nname=" + name +
                "\nipadress='" + ipadress + '\'' +
                "\nserverPublicKey='" + serverPublicKey + '\'' +
                "\ndns='" + dns + '\'' +
                "\n}";
    }
}
