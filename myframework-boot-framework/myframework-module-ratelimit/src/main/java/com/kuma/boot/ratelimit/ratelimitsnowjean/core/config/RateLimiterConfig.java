/*
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.kuma.boot.ratelimit.ratelimitsnowjean.core.config;

import com.kuma.boot.ratelimit.ratelimitsnowjean.core.exception.SnowJeanException;
import com.kuma.boot.ratelimit.ratelimitsnowjean.core.ticket.TicketServer;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RateLimiterConfig {
    private static volatile RateLimiterConfig rateLimiterConfig;
    private static Logger logger;
    private TicketServer ticketServer;
    private ScheduledExecutorService scheduledThreadExecutor;
    public static String http_monitor;
    public static String http_heart;
    public static String http_token;

    private RateLimiterConfig() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static RateLimiterConfig getInstance() {
        if (rateLimiterConfig != null) return rateLimiterConfig;
        Class<RateLimiterConfig> clazz = RateLimiterConfig.class;
        synchronized (RateLimiterConfig.class) {
            if (rateLimiterConfig != null) return rateLimiterConfig;
            rateLimiterConfig = new RateLimiterConfig();
            logger.info("Starting [SnowJean]");
            // ** MonitorExit[var0] (shouldn't be in output)
            return rateLimiterConfig;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ScheduledExecutorService getScheduledThreadExecutor() {
        if (this.scheduledThreadExecutor == null) {
            RateLimiterConfig rateLimiterConfig = this;
            synchronized (rateLimiterConfig) {
                if (this.scheduledThreadExecutor == null) {
                    this.setScheduledThreadExecutor(new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2, new ThreadPoolExecutor.DiscardOldestPolicy()));
                }
            }
        }
        return this.scheduledThreadExecutor;
    }

    public void setScheduledThreadExecutor(ScheduledExecutorService scheduledThreadExecutor) {
        this.scheduledThreadExecutor = scheduledThreadExecutor;
    }

    public TicketServer getTicketServer() {
        if (this.ticketServer == null) {
            throw new SnowJeanException("error: ticketServer == null");
        }
        return this.ticketServer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setTicketServer(Map<String, Integer> ip) {
        if (ip.size() < 1) {
            throw new SnowJeanException("ip.size()<1 is not pass!");
        }
        if (this.ticketServer == null) {
            RateLimiterConfig rateLimiterConfig = this;
            synchronized (rateLimiterConfig) {
                if (this.ticketServer == null) {
                    this.ticketServer = new TicketServer();
                }
            }
        }
        this.ticketServer.setServer(ip);
    }

    static {
        logger = LoggerFactory.getLogger(RateLimiterConfig.class);
        http_monitor = "monitor";
        http_heart = "heart";
        http_token = "token";
    }
}

