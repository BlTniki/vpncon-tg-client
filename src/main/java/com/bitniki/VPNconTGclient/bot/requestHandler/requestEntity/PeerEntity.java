package com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity;

@SuppressWarnings("unused")
public class PeerEntity {
    private Long id;
    private String peerIp;
    private String peerPrivateKey;
    private String peerPublicKey;
    private String peerConfName;
    private HostEntity host;

    public PeerEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPeerIp() {
        return peerIp;
    }

    public void setPeerIp(String peerIp) {
        this.peerIp = peerIp;
    }

    public String getPeerPrivateKey() {
        return peerPrivateKey;
    }

    public void setPeerPrivateKey(String peerPrivateKey) {
        this.peerPrivateKey = peerPrivateKey;
    }

    public String getPeerPublicKey() {
        return peerPublicKey;
    }

    public void setPeerPublicKey(String peerPublicKey) {
        this.peerPublicKey = peerPublicKey;
    }

    public String getPeerConfName() {
        return peerConfName;
    }

    public void setPeerConfName(String peerConfName) {
        this.peerConfName = peerConfName;
    }

    public HostEntity getHost() {
        return host;
    }

    public void setHost(HostEntity host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return "\nPeerEntity{" +
                "\nid = " + id +
                "\npeerIp = " + peerIp +
                "\npeerPrivateKey = " + peerPrivateKey +
                "\npeerPublicKey = " + peerPublicKey +
                "\npeerConfName = " + peerConfName +
                "\nhost = " + host.getName() +
                "\n}";
    }
}
