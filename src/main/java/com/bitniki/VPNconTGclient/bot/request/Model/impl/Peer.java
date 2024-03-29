package com.bitniki.VPNconTGclient.bot.request.Model.impl;

import com.bitniki.VPNconTGclient.bot.request.Model.Model;
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
    private UserEntity user;
    private Host host;
}
