package de.erethon.bedrock.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.erethon.bedrock.chat.MessageUtil;
import org.jdbi.v3.core.HandleCallback;
import org.jdbi.v3.core.HandleConsumer;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import javax.sql.DataSource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public abstract class EDatabaseManager {

    protected final Jdbi jdbi;
    protected final DataSource dataSource;
    protected final Executor asyncExecutor;
    private final CompletableFuture<Void> initializationFuture;

    protected EDatabaseManager(BedrockDBConnection connection, Executor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(connection.url());
        hikariConfig.setDataSourceClassName(connection.dataSourceClassName());
        hikariConfig.addDataSourceProperty("user", connection.user());
        hikariConfig.addDataSourceProperty("password", connection.password());

        hikariConfig.setMaximumPoolSize(connection.maximumPoolSize());
        hikariConfig.setMinimumIdle(connection.minimumIdle());
        hikariConfig.setConnectionTimeout(connection.connectionTimeout());
        hikariConfig.setIdleTimeout(connection.idleTimeout());
        hikariConfig.setMaxLifetime(connection.maxLifetime());

        this.dataSource = new HikariDataSource(hikariConfig);
        this.jdbi = Jdbi.create(this.dataSource);

        configureJdbiPlugins(jdbi);
        registerCustomMappers();

        MessageUtil.log("EDatabaseManager: Starting schema initialization asynchronously...");
        this.initializationFuture = initializeSchema();

        this.initializationFuture.whenCompleteAsync((result, ex) -> {
            if (ex != null) {
                MessageUtil.log("!!! CRITICAL: Database schema initialization failed !!!");
                ex.printStackTrace();
            } else {
                MessageUtil.log("Database schema initialization completed successfully asynchronously.");
            }
        }, asyncExecutor);

        MessageUtil.log("EDatabaseManager: Constructor finished. Schema initialization running in background.");

    }

    /**
     * Configures necessary JDBI plugins. Override to add more.
     * @param jdbiInstance The JDBI instance to configure.
     */
    protected void configureJdbiPlugins(Jdbi jdbiInstance) {
        jdbiInstance.installPlugin(new SqlObjectPlugin()); // For DAOs
        jdbiInstance.installPlugin(new PostgresPlugin());   // For PostgreSQL specific types (like UUID, Array)
    }

    /**
     * Implement this method to create or migrate the database schema.
     * This method is called during initialization.
     * @return A CompletableFuture indicating completion.
     */
    protected abstract CompletableFuture<Void> initializeSchema();

    /**
     * Implement this method to register custom Mappers or ArgumentFactories.
     * This method is called after JDBI is initialized.
     */
    protected abstract void registerCustomMappers();


    // --- Async Execution Helpers ---

    /**
     * Executes a JDBI operation that doesn't return a value asynchronously.
     * The provided consumer can throw RuntimeExceptions.
     *
     * @param consumer A JDBI HandleConsumer.
     * @return A CompletableFuture<Void>.
     */
    public CompletableFuture<Void> executeAsync(HandleConsumer<RuntimeException> consumer) {
        return initializationFuture.thenRunAsync(() -> jdbi.useHandle(consumer), asyncExecutor);
    }

    /**
     * Executes a JDBI Handle operation asynchronously (e.g., calling a non-transactional DAO method
     * or executing raw SQL) that returns a value.
     * The provided callback can throw RuntimeExceptions.
     *
     * @param callback A JDBI HandleCallback.
     * @param <R>      The return type.
     * @return A CompletableFuture<R>.
     */
    public <R> CompletableFuture<R> queryAsync(HandleCallback<R, RuntimeException> callback) {
        return initializationFuture.thenApplyAsync(v -> jdbi.withHandle(callback), asyncExecutor);
    }

    /**
     * Executes a JDBI transaction asynchronously that returns a value.
     * The callback receives a Handle and can perform multiple operations within the transaction.
     * The provided callback can throw RuntimeExceptions.
     *
     * @param callback A JDBI TransactionCallback (from org.jdbi.v3.core.transaction).
     * @param <R>      The return type.
     * @return A CompletableFuture<R>.
     */
    public <R> CompletableFuture<R> transactionAsync(HandleCallback<R, RuntimeException> callback) {
        return initializationFuture.thenApplyAsync(v -> jdbi.inTransaction(callback), asyncExecutor);
    }

    /**
     * Executes a JDBI transaction asynchronously that doesn't return a value.
     * The consumer receives a Handle and can perform multiple operations within the transaction.
     * The provided consumer can throw RuntimeExceptions.
     *
     * @param consumer A JDBI TransactionConsumer (from org.jdbi.v3.core.transaction).
     * @return A CompletableFuture<Void>.
     */
    public CompletableFuture<Void> useTransactionAsync(HandleConsumer<RuntimeException> consumer) {
        return initializationFuture.thenRunAsync(() -> jdbi.useTransaction(consumer), asyncExecutor);
    }

    // --- DAO Access ---

    /**
     * Gets a JDBI DAO (SqlObject) instance.
     * Ensure the SqlObjectPlugin is installed.
     *
     * @param daoClass The class of the DAO interface.
     * @param <T>      The type of the DAO.
     * @return An instance of the DAO.
     */
    protected <T> T getDao(Class<T> daoClass) {
        return jdbi.onDemand(daoClass);
    }

    /**
     * Closes the underlying DataSource.
     */
    public void close() {
        try {
        } catch (Exception e) {
            MessageUtil.log("Error or timeout waiting for pending operations during close: " + e.getMessage());
        }

        if (this.dataSource instanceof HikariDataSource) {
            ((HikariDataSource) this.dataSource).close();
            MessageUtil.log("Database connection pool closed.");
        }
    }

    public boolean isInitialized() {
        return initializationFuture != null && initializationFuture.isDone() && !initializationFuture.isCompletedExceptionally();
    }

    public CompletableFuture<Void> getInitializationFuture() {
        return initializationFuture;
    }
}