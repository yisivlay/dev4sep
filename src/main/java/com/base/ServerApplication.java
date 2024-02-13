package com.base;

import com.base.infrastructure.core.boot.AbstractServerApplication;
import com.base.infrastructure.core.boot.DataSourceConfig;
import com.base.infrastructure.core.boot.ExitUtil;
import com.base.infrastructure.core.config.TomcatConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

import java.io.IOException;

public class ServerApplication {

    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext ctx = SpringApplication.run(Configuration.class, args);
        ExitUtil.waitForKeyPressToCleanlyExit(ctx);
    }

    @Import({TomcatConfig.class, DataSourceConfig.class})
    public static class Configuration extends AbstractServerApplication {
    }

}
