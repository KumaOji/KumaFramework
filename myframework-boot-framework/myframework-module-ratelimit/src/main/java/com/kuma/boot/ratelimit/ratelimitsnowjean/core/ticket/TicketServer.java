/*
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.kuma.boot.ratelimit.ratelimitsnowjean.core.ticket;

import com.kuma.boot.ratelimit.ratelimitsnowjean.commoon.http.HttpUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TicketServer {
    private Logger logger = LoggerFactory.getLogger(TicketServer.class);
    private List<String> serverList = new CopyOnWriteArrayList<String>();
    private List<String> backupsList = new CopyOnWriteArrayList<String>();
    private ReentrantLock lock = new ReentrantLock();
    private int pos = 0;
    private long start = 0L;

    public void setServer(Map<String, Integer> ip) {
        this.serverList.clear();
        HashMap<String, Integer> serverMap = new HashMap<String, Integer>(ip);
        for (String server : serverMap.keySet()) {
            int weight = (Integer)serverMap.get(server);
            for (int i = 0; i < weight; ++i) {
                this.serverList.add(server);
            }
        }
    }

    private String getServer() {
        String server;
        this.lock.lock();
        try {
            if (this.serverList.size() == 0) {
                this.serverList.addAll(this.backupsList);
                this.backupsList.clear();
            }
            if (this.pos >= this.serverList.size()) {
                this.pos = 0;
            }
            server = this.serverList.get(this.pos);
            ++this.pos;
        }
        finally {
            this.lock.unlock();
        }
        return server;
    }

    public String connect(String path, String data) {
        String server = this.getServer();
        try {
            return HttpUtil.connect("http://" + server + "/" + path).setData("data", data).setMethod("POST").execute().getBody(new String[0]);
        }
        catch (IOException e) {
            if (System.currentTimeMillis() - this.start > 3000L) {
                this.logger.error("{} The server is not available.", (Object)server);
                this.start = System.currentTimeMillis();
            }
            this.serverList.remove(server);
            this.backupsList.add(server);
            return null;
        }
    }
}

