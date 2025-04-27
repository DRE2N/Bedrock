package de.erethon.bedrock.database;

public record BedrockDBConnection(String url, String user, String password, String dataSourceClassName, int maximumPoolSize, int minimumIdle, int connectionTimeout, int idleTimeout, int maxLifetime) {

    public BedrockDBConnection(String url, String user, String password, String dataSourceClassName) {
        this(url, user, password, dataSourceClassName, 10, 5, 30000, 600000, 1800000);
    }

    public BedrockDBConnection(String url, String user, String password, String dataSourceClassName, int maximumPoolSize) {
        this(url, user, password, dataSourceClassName, maximumPoolSize, 5, 30000, 600000, 1800000);
    }

    public BedrockDBConnection(String url, String user, String password, String dataSourceClassName, int maximumPoolSize, int minimumIdle) {
        this(url, user, password, dataSourceClassName, maximumPoolSize, minimumIdle, 30000, 600000, 1800000);
    }
}
