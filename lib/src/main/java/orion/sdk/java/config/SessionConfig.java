package orion.sdk.java.config;

import java.time.Duration;

public class SessionConfig {
    UserConfig userConfig;
    Duration txTimeout;
    Duration queryTimeout;

    public SessionConfig(UserConfig userConfig, Duration txTimeout, Duration queryTimeout) {
        this.userConfig = userConfig;
        this.txTimeout = txTimeout;
        this.queryTimeout = queryTimeout;
    }

    public UserConfig getUserConfig() {
        return this.userConfig;
    }

    public Duration getTxTimeout() {
        return this.txTimeout;
    }

    public Duration getQueryTimeout() {
        return this.queryTimeout;
    }
}
