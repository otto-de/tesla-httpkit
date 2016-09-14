package org.httpkit.server;

import java.io.IOException;

public class TeslaHttpServer extends HttpServer {

    private TeslaRingHandler teslaHandler;

    public TeslaHttpServer(String ip, int port, TeslaRingHandler handler, int maxBody, int maxLine, int maxWs) throws IOException {
        super(ip, port, handler, maxBody, maxLine, maxWs);
        this.teslaHandler = handler;
    }

    public TeslaRingHandler getHandler() {
        return this.teslaHandler;
    }

}
