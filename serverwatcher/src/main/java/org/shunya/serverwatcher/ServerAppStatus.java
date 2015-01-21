package org.shunya.serverwatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.context.request.async.DeferredResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class ServerAppStatus {
    private static final Logger logger = Logger.getLogger(ServerAppStatus.class.getName());

    private final AtomicLong cacheId;
    private final List<ServerApp> serverApps;
    private final ConcurrentMap<Long, List<DeferredResult>> deferredResultConcurrentMap = new ConcurrentHashMap<>();

    public ServerAppStatus(List<ServerApp> serverApps, long atomicLong) {
        this.serverApps = serverApps;
        this.cacheId = new AtomicLong(atomicLong);
    }

    public void updateCacheId() {
        cacheId.incrementAndGet();
        deferredResultConcurrentMap.forEach((serverId, deferredResults) -> {
            try {
                ServerAppStatus serverAppStatus = fetchStatus(serverId);
                ObjectMapper mapper = new ObjectMapper();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                mapper.writeValue(baos, serverAppStatus);
                synchronized (deferredResults) {
                    deferredResults.forEach(deferredResult -> deferredResult.setResult(baos.toString()));
                    deferredResults.clear();
                }
            } catch (IOException e) {
                logger.warning(StringUtils.getExceptionHeaders(e));
            }
        });
    }

    public void subscribe(long serverId, DeferredResult<String> deferredResult) {
        deferredResultConcurrentMap.computeIfAbsent(serverId, aLong -> new Vector<>()).add(deferredResult);
    }

    public ServerAppStatus fetchStatus(Long serverId) {
        if (serverId == 0) {
            return new ServerAppStatus(Collections.unmodifiableList(serverApps), getCacheId());
        }
        int index = Collections.binarySearch(serverApps, new ServerAppBuilder().withId(serverId).build());
        if (index >= 0) {
            ServerApp serverApp = serverApps.get(index);
            return new ServerAppStatus(Arrays.asList(serverApp), getCacheId());
        }
        return null;
    }

    public long getCacheId() {
        return cacheId.get();
    }

    public List<ServerApp> getServerApps() {
        return serverApps;
    }
}