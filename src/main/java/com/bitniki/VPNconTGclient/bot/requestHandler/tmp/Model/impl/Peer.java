package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.Model;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Peer extends Model {
    private Long id;
    private String peerConfName;
    private String peerIp;
    private Boolean isActivated;
    private User user;
    private Host host;
}
