package org.httpkit.server;

import clojure.lang.IFn;

import java.util.concurrent.ThreadPoolExecutor;

public class TeslaRingHandler extends RingHandler {

    public TeslaRingHandler(int thread, IFn handler, String prefix, int queueSize) {
        super(thread, handler, prefix, queueSize);
    }

    public ThreadPoolExecutor getExecutor() {
        return (ThreadPoolExecutor) this.execs;
    }
}