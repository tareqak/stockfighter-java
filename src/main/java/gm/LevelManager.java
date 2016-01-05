package gm;

import gm.requests.LevelHeartbeatRequest;
import gm.requests.StartLevelRequest;
import gm.requests.StopLevelRequest;
import gm.responses.LevelHeartbeatResponse;
import gm.responses.LevelResponse;
import gm.responses.abstractions.Details;
import gm.responses.abstractions.Flash;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utilities.Utilities;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LevelManager implements Closeable {
    private static final Logger logger =
            LoggerFactory.getLogger(LevelManager.class);

    private final int secondsInATradingDay;
    private final LevelResponse levelResponse;
    private final Integer instanceId;
    private final String levelName;
    private final ReentrantReadWriteLock flashReentrantReadWriteLock =
            new ReentrantReadWriteLock();
    private final Thread levelHeartbeatCheckerThread;
    private final AtomicBoolean done = new AtomicBoolean(false);
    private final CloseableHttpClient httpClient;
    private Flash flash = null;

    public LevelManager(final CloseableHttpClient httpClient,
                        final String levelName) {
        this.levelName = levelName;
        this.httpClient = httpClient;
        levelHeartbeatCheckerThread = new Thread(new LevelHeartbeatChecker());
        StartLevelRequest startLevelRequest =
                new StartLevelRequest(httpClient, levelName);
        boolean first = true;
        while (true) {
            LevelResponse levelResponse = (LevelResponse)
                    startLevelRequest.getResponse();
            if (levelResponse != null) {
                String error = levelResponse.getError();
                if (error != null) {
                    logger.error(error);
                    if (error.contains("You hit the rate limit.")) {
                        try {
                            for (int i = 0; i < 95; i += 5) {
                                logger.info("{} s", i);
                                Thread.sleep(5 *
                                        Utilities.MILLISECONDS_IN_A_SECOND);
                            }
                        } catch (InterruptedException e) {
                            logger.error("Error while sleeping.", e);
                        }
                    }
                    continue;
                }
                if (first) {
                    logger.debug("Abandoning first instance for a fresh start");
                    StopLevelRequest stopLevelRequest =
                            new StopLevelRequest(httpClient,
                                    levelResponse.getInstanceId());
                    logger.debug(stopLevelRequest.getResponse().toString());
                    first = false;
                } else {
                    this.instanceId = levelResponse.getInstanceId();
                    this.levelResponse = levelResponse;
                    this.secondsInATradingDay =
                            levelResponse.getSecondsPerTradingDay();
                    logger.info("Started level \"{}\" with instanceId {}.",
                            levelName, instanceId);
                    break;
                }
            }
        }
        logger.info("Starting levelHeartbeatCheckerThread.");
        levelHeartbeatCheckerThread.start();
    }

    public Flash getFlash() {
        try {
            flashReentrantReadWriteLock.readLock().lock();
            return flash;
        } finally {
            flashReentrantReadWriteLock.readLock().unlock();
        }
    }

    public LevelResponse getLevelResponse() {
        return levelResponse;
    }

    @Override
    public void close() {
        try {
            done.set(true);
            logger.info("Stopping levelHeartbeatCheckerThread.");
            levelHeartbeatCheckerThread.join();
        } catch (InterruptedException e) {
            logger.error("Error stopping levelHeartbeatCheckerThread.", e);
        }
        logger.info("Stopping \"{}\".", levelName);
        StopLevelRequest stopLevelRequest =
                new StopLevelRequest(httpClient, instanceId);
        logger.info(stopLevelRequest.getResponse().toString());
    }

    public boolean isLevelComplete() {
        return done.get();
    }

    public int getSecondsInATradingDay() {
        return secondsInATradingDay;
    }

    private class LevelHeartbeatChecker implements Runnable {
        @Override
        public void run() {
            LevelHeartbeatRequest levelHeartbeatRequest =
                    new LevelHeartbeatRequest(httpClient, instanceId);
            Flash previousFlash = null;
            while (true) {
                LevelHeartbeatResponse levelHeartbeatResponse =
                        (LevelHeartbeatResponse)
                                levelHeartbeatRequest.getResponse();
                final Details details = levelHeartbeatResponse.getDetails();
                if (details != null) {
                    final Integer tradingDay = details.getTradingDay();
                    final Integer endOfTheWorldDay =
                            details.getEndOfTheWorldDay();
                    logger.info("Day {}", tradingDay);
                    if (tradingDay.equals(endOfTheWorldDay)) {
                        logger.info("Reached end of the world: {}.",
                                endOfTheWorldDay);
                        done.set(true);
                        break;
                    }
                }
                logger.debug(levelHeartbeatResponse.toString());
                final Flash flash = levelHeartbeatResponse.getFlash();
                if (flash != null && (previousFlash == null ||
                        !previousFlash.equals(flash))) {
                    logger.info("FLASH {}", flash);
                    flashReentrantReadWriteLock.writeLock().lock();
                    LevelManager.this.flash = flash;
                    flashReentrantReadWriteLock.writeLock().unlock();
                    previousFlash = flash;
                }
                String state = levelHeartbeatResponse.getState();
                if (state != null && (state.equals("won") ||
                        state.equals("lost"))) {
                    done.set(true);
                    logger.info("Received state \"{}\".", state);
                }
                if (done.get()) {
                    break;
                }
                try {
                    Thread.sleep(secondsInATradingDay *
                            Utilities.MILLISECONDS_IN_A_SECOND);
                } catch (InterruptedException e) {
                    logger.error("Error while sleeping.", e);
                }
            }
        }
    }
}
