package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.Model;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeerValidatorRegex extends Model {
    private String peerIp;
    private String peerConfName;
}
