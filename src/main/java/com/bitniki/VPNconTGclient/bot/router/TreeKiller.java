package com.bitniki.VPNconTGclient.bot.router;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

class TreeKiller {
    final Long chatId;
    final int killTimeInSec = 5 * 60; //5 min
    final UpdateRouter updateRouter;
    ScheduledFuture<?> beeperKillerHandle;
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public TreeKiller(Long chatId, UpdateRouter updateRouter) {
        this.chatId = chatId;
        this.updateRouter = updateRouter;
        killTreeAfter5min();
    }

    private Runnable makeKiller() {
        return () -> updateRouter.deleteChatFromMaps(chatId);
    }

    public void killTreeAfter5min() {
        beeperKillerHandle = scheduler.schedule(makeKiller(), killTimeInSec, SECONDS);
    }

    public void refreshKillTime() {
        beeperKillerHandle.cancel(true);
        beeperKillerHandle = scheduler.schedule(makeKiller(), killTimeInSec, SECONDS);
    }
}
