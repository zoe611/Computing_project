package au.edu.unimelb.sri.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * Created by sulman on 8/02/2017.
 */
public class SqlConnection {

    private static String DRIVER_CLASS_PGSIMPLE = "org.postgresql.ds.PGSimpleDataSource";
    private static String DRIVER_CLASS_PGPOOLING = "org.postgresql.ds.PGPoolingDataSource";
    private static String DRIVER_CLASS = "org.postgresql.Driver";
    private static String DRIVER_CLASS_SPY = "net.sf.log4jdbc.sql.jdbcapi.DriverSpy";

    private static String DB_URL = "jdbc:log4jdbc:jdbc:postgresql://localhost/spinal_pubmed" +
            "?loglevel=2&logUnclosedConnections=true";

    private static String DB_URL_1 = "jdbc:log4jdbc:postgresql://localhost/spinal_pubmed" +
            "?loglevel=2&logUnclosedConnections=true";

    private static String DB_URL_2 = "jdbc:log4jdbc:postgresql://localhost/spinal_pubmed" +
            "?loglevel=2&logUnclosedConnections=true";

    private static String USERNAME = "postgres";
    private static String PASSWORD = "";
    private static int MAX_POOL_SIZE = 20;
    private static int MIN_POOL_SIZE = 1;
    private static int MIN_IDLE = 2;
    private static boolean AUTO_COMMIT = false;

    private static DataSource dataSource;

    private static Connection connection;

    /**
     * no argument public constructor
     */
    public SqlConnection() {
    }

    private static DataSource getDataSource() {
        if (dataSource == null) {

//            ComboPooledDataSource cpds = new ComboPooledDataSource();
//            try {
//                cpds.setDriverClass("org.postgresql.Driver"); //loads the jdbc driver
//            } catch (PropertyVetoException e) {
//                e.printStackTrace();
//            }
//            cpds.setJdbcUrl(DB_URL);
//            cpds.setUser(USERNAME);
//            cpds.setPassword(PASSWORD);
//            cpds.setMaxStatements(180);
//
//            cpds.setMinPoolSize(3);
//            cpds.setAcquireIncrement(1);
//            cpds.setMaxPoolSize(10);
//            cpds.setTestConnectionOnCheckin(true);
//            cpds.setIdleConnectionTestPeriod(300);
//            cpds.setMaxIdleTimeExcessConnections(240);

            final HikariConfig hikariConfig = new HikariConfig();
            try {
                Class.forName(DRIVER_CLASS_SPY);
                Class.forName(DRIVER_CLASS);
                Class.forName(DRIVER_CLASS_PGSIMPLE);
                Class.forName(DRIVER_CLASS_PGPOOLING);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            hikariConfig.setDriverClassName(DRIVER_CLASS_SPY);
            hikariConfig.setJdbcUrl(DB_URL_1);
            hikariConfig.setUsername(USERNAME);
            hikariConfig.setPassword(PASSWORD);

            hikariConfig.setMaximumPoolSize(MAX_POOL_SIZE);

//            hikariConfig.setAutoCommit(AUTO_COMMIT);
            hikariConfig.setLeakDetectionThreshold(1000);

            hikariConfig.setMinimumIdle(MIN_IDLE);
            hikariConfig.setConnectionTimeout(3000);
            hikariConfig.setIdleTimeout(TimeUnit.SECONDS.toMillis(10));
            hikariConfig.setValidationTimeout(TimeUnit.SECONDS.toMillis(2));

            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            //hikariConfig.setMaxLifetime(60000);

            //hikariDataSource.setIdleTimeout(60000);
            //hikariDataSource.setConnectionTimeout(60000);
            //hikariDataSource.setValidationTimeout(3000);
            //hikariDataSource.setLoginTimeout(5);

            dataSource = new HikariDataSource(hikariConfig);
//            dataSource = cpds;

            // dataSource = new BasicDataSource();
        }

        return dataSource;
    }

    /**
     * create and return a new SqlCommand
     *
     * @return
     */
    public SqlCommand createSqlCommand() throws SQLException {
        SqlCommand cmd = new SqlCommand();
        cmd.setConnection(getDataSource().getConnection());
        return cmd;
    }

    public Connection getDatabaseConnection() throws SQLException {
//        if (getDataSource().getConnection().isClosed()) {
//            System.out.println("connection is closed.");
//        }
        //return getDataSource().getConnection();
        //return connection;
        return getConnection();

    }

    public static Connection getConnection()throws SQLException{
        if(connection==null){
            connection = DriverManager.getConnection(DB_URL_1);
        }
        return connection;
    }


    public void close() throws SQLException {
        //if (!getDataSource().getConnection().isClosed()) {
        //getDataSource().getConnection().close();
        //connection.close();
        //}
    }
}
