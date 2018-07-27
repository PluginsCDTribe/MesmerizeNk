package it.alian.gun.mesmerize;


import cn.nukkit.Server;

import java.util.concurrent.*;

public class MTasks {

    private static ScheduledExecutorService service;

    public static <T> Future<T> submit(Callable<T> callable) {
        return service.submit(callable);
    }

    public static void runTimer(Runnable runnable, long period) {
        Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(Mesmerize.instance, runnable, 0, ((int) period));
    }

    public static void runLater(Runnable runnable, long delay) {
        Server.getInstance().getScheduler().scheduleDelayedTask(Mesmerize.instance, runnable, ((int) delay));
    }

    public static void runLater(Runnable runnable) {
        Server.getInstance().getScheduler().scheduleTask(Mesmerize.instance, runnable);
    }

    public static void execute(Runnable runnable) {
        service.execute(runnable);
    }

    public static void executeTimer(Runnable runnable, long period) {
        service.scheduleAtFixedRate(runnable, 0, period, TimeUnit.MILLISECONDS);
    }

    public static void executeLater(Runnable runnable, long delay) {
        service.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    public static void init() {
        service = Executors.newScheduledThreadPool(MConfig.Performance.workerThreads);
    }

    public static void unload() {
        service.shutdownNow();
    }

}
