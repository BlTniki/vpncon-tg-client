package com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.impl;

import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.Peer;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.Model.impl.PeerValidatorRegex;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.ModelForRequest.impl.PeerForRequest;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.RequestService.PeerRequestService;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.ModelNotFoundException;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.ModelValidationFailedException;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.requestHandler.RequestHandler400Exception;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.exception.requestHandler.RequestHandler404Exception;
import com.bitniki.VPNconTGclient.bot.requestHandler.tmp.requestHandle.RequestHandler;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class PeerRequestServiceImpl implements PeerRequestService {
    private final RequestHandler requestHandler;

    public PeerRequestServiceImpl(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public List<Peer> getPeersByUserId(Long userId) {
        final String ENDPOINT = "/peers/byUser/" + userId;

        return List.of(requestHandler.GET(ENDPOINT, Peer[].class));
    }

    @Override
    public Peer getPeerByPeerIpAndHostId(String peerIp, Long hostId) throws ModelNotFoundException {
        final String ENDPOINT = "/peers/byField?peerIp=%s&hostId=%d".formatted(peerIp, hostId);

        try {
            return requestHandler.GET(ENDPOINT, Peer.class);
        } catch (RequestHandler404Exception e) {
            throw new ModelNotFoundException(
                    "Не смог найти пира с peerIp = %s и hostId = %d".formatted(peerIp, hostId)
            );
        }
    }

    @Override
    public PeerValidatorRegex getValidatorFields() {
        final String ENDPOINT = "/peers/validator";

        return requestHandler.GET(ENDPOINT, PeerValidatorRegex.class);
    }

    @Override
    public Peer createPeerOnServer(PeerForRequest model) throws ModelValidationFailedException, ModelNotFoundException {
        final String ENDPOINT = "/peers";

        try {
            return requestHandler.POST(ENDPOINT, model, Peer.class);
        } catch (RequestHandler400Exception e) {
            throw new ModelValidationFailedException("Некоторые данные указаны неверно:\n" + e.getMessage());
        } catch (RequestHandler404Exception e) {
            throw new ModelNotFoundException(e.getMessage());
        }
    }

    @Override
    public Boolean activatePeer(Long peerId) throws ModelNotFoundException {
        final String ENDPOINT = "/peers/activate/" + peerId;

        try {
            return requestHandler.POST(ENDPOINT, null, Boolean.class);
        } catch (RequestHandler404Exception e) {
            throw new ModelNotFoundException("Пир с id %d не найден".formatted(peerId));
        }
    }

    @Override
    public Boolean deactivatePeer(Long peerId) throws ModelNotFoundException {
        final String ENDPOINT = "/peers/deactivate/" + peerId;

        try {
            return requestHandler.POST(ENDPOINT, null, Boolean.class);
        } catch (RequestHandler404Exception e) {
            throw new ModelNotFoundException("Пир с id %d не найден".formatted(peerId));
        }
    }

    @Override
    public InputFile getConfigFileFromServer(Peer peer) throws ModelNotFoundException {
        final String DOWNLOAD_TOKEN_ENDPOINT = "/peers/conf/" + peer.getId();

        // get download token
        String downloadToken;
        try {
            downloadToken = requestHandler.GET(DOWNLOAD_TOKEN_ENDPOINT, String.class);
        } catch (RequestHandler404Exception e) {
            throw new ModelNotFoundException("Пир с id %d не найден".formatted(peer.getId()));
        }

        final String FILE_URI = "http://"
                + peer.getHost().getIpaddress()
                + ":" + peer.getHost().getPort()
                + "/api/1.0/conf/"
                + downloadToken;

        // get file
        try {
            return new InputFile(
                    (new URL(FILE_URI)).openStream(),
                    peer.getPeerConfName() + ".conf"
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Peer deletePeerOnServer(Long peerId) throws ModelNotFoundException {
        final String ENDPOINT = "/peers";

        try {
            return requestHandler.DELETE(ENDPOINT, Peer.class);
        } catch (RequestHandler404Exception e) {
            throw new ModelNotFoundException("Пир с id %d не найден".formatted(peerId));
        }
    }

    @Override
    public Boolean isPeerIpAlreadyExistOnHost(String peerIp, Long hostId) {
        try {
            getPeerByPeerIpAndHostId(peerIp, hostId);
            // on success to load an entity return true
            return Boolean.TRUE;
        } catch (ModelNotFoundException e) {
            // else return false
            return Boolean.FALSE;
        }
    }
}
