package cz.bvv.errorhandlingdemo.ftp;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.apache.ftpserver.ConnectionConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Configuration
public class FtpServerConfig {
    private final FtpRuntimeSettings ftpRuntimeSettings;

    public FtpServerConfig(FtpRuntimeSettings ftpRuntimeSettings) {
        this.ftpRuntimeSettings = ftpRuntimeSettings;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public FtpServer ftpServer() throws Exception {

        FtpServerFactory factory = new FtpServerFactory();

        // Listener
        ListenerFactory lf = new ListenerFactory();
        lf.setPort(ftpRuntimeSettings.port());
        DataConnectionConfigurationFactory dccf = new DataConnectionConfigurationFactory();
        dccf.setActiveEnabled(false);
        dccf.setPassivePorts("2200-2210");
        dccf.setPassiveIpCheck(false);
        lf.setDataConnectionConfiguration(dccf.createDataConnectionConfiguration());
        factory.addListener("default", lf.createListener());

        // Connection config
        ConnectionConfigFactory ccf = new ConnectionConfigFactory();
        ccf.setAnonymousLoginEnabled(false);
        ccf.setMaxAnonymousLogins(0);
        ccf.setMaxLogins(0);
        ccf.setMaxThreads(100);
        factory.setConnectionConfig(ccf.createConnectionConfig());

        // Home dir
        Files.createDirectories(new File("ftp-home").toPath());

        // User manager
        UserManager userManager = new UserManager();

        BaseUser user = new BaseUser();
        user.setName("user");
        user.setPassword("password");
        user.setHomeDirectory(new File("ftp-home").getAbsolutePath());
        user.setAuthorities(List.of(
                new WritePermission(),
                new ConcurrentLoginPermission(0, 0)
        ));

        userManager.save(user);
        factory.setUserManager(userManager);

        return factory.createServer();
    }
}
