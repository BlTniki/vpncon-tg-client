package com.bitniki.VPNconTGclient.bot.requestHandler.requestEntity;

import java.util.List;

@SuppressWarnings("unused")
public class HostEntity {
    private Long id;
    private String name;
    private String ipadress;
    private String serverPublicKey;
    private String dns;
    private List<PeerEntity> peers;

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

    public List<PeerEntity> getPeers() {
        return peers;
    }

    public void setPeers(List<PeerEntity> peers) {
        this.peers = peers;
    }

    /**
     * Count number of peers and subtracts from 254 — maximum possible number
     *     peers can be null, in this case method return zero
     * @return number of available peers
     */
    public int getAvailablePeersCount() {
        if(peers != null) {
            return 254 - peers.size();
        }
        return 0;
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
