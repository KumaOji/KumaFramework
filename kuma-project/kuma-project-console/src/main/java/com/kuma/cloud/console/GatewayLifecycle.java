package com.kuma.cloud.console;

import tools.jackson.databind.ObjectMapper;
import java.nio.file.Path;

/** Windows Gateway lifecycle using the local Java service launcher. */
public class GatewayLifecycle extends LocalJavaService {
    public GatewayLifecycle(Path root, String distribution, CommandRunner commands) {
        super(root.toString(), distribution, commands, new ObjectMapper(), "gateway");
    }
}
