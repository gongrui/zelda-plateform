package com.blue.platform.uaa;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import com.blue.zelda.fw.security.annotation.EnableOAuth2Server;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import java.net.InetAddress;

/**
 * 鉴权服务启动类.
 *
 * @author gongrui
 */

@Slf4j
@EnableCaching
@SpringBootApplication
@EnableFeignClients
@MapperScan("com.blue.platform.uaa.**.mapper")
@ComponentScan(basePackages = {"com.blue.platform.uaa", "com.blue.zelda.fw"})
public class UaaApplication {

    /**
     * 启动类.
     *
     * @param args 启动参数
     */
    @SneakyThrows
    public static void main(final String[] args) {
        final ConfigurableApplicationContext applicationContext = SpringApplication.run(UaaApplication.class, args);
        Environment env = applicationContext.getEnvironment();
        final String appName = env.getProperty("spring.application.name");
        String host = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        log.info("""
                
                ----------------------------------------------------------
                \tApplication '{}' is running! Access URLs:
                \tDoc: \thttp://{}:{}/doc.html
                
                \tSa-Token-OAuth2 Server端启动成功，配置如下：
                \t端口：{}
                \t登录地址：http://{}:{}/oauth2/authorize
                \t{}
                ----------------------------------------------------------""",
                appName, host, port,SaOAuth2Manager.getServerConfig());
    }
}
