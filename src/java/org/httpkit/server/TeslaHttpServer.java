package org.httpkit.server;

import java.io.IOException;

public class TeslaHttpServer extends HttpServer {

    private TeslaRingHandler teslaHandler;

    public TeslaHttpServer(String ip, int port, IHandler handler, int maxBody, int maxLine, int maxWs, ProxyProtocolOption proxyProtocolOption) throws IOException {
        super(ip, port, handler, maxBody, maxLine, maxWs, proxyProtocolOption);
        this.teslaHandler = (TeslaRingHandler) handler;
    }

    public TeslaRingHandler getHandler() {
        return this.teslaHandler;
    }

}
