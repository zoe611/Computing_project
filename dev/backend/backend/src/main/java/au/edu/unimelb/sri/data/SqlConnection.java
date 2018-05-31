package au.edu.unimelb.sri.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.*;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;


public class SqlConnection {

    private static String DRIVER_CLASS_PGSIMPLE = "org.postgresql.ds.PGSimpleDataSource";
    private static String DRIVER_CLASS_PGPOOLING = "org.postgresql.ds.PGPoolingDataSource";
    private static String DRIVER_CLASS = "org.postgresql.Driver";
    private static String DRIVER_CLASS_SPY = "net.sf.log4jdbc.sql.jdbcapi.DriverSpy";

//    private static String DB_URL = "jdbc:mysql://localhost:/spinal_pubmed";

    private static String DB_URL_1 = "jdbc:log4jdbc:postgresql://localhost/spinal_pubmed" +
            "?loglevel=2&logUnclosedConnections=true";

	private static String JDBC_DRIVER = "com.mysql.jdbc.Driver"; 
	private static String DB_URL = "jdbc:mysql://localhost/spinal_pubmed";
    private static String USERNAME = "root";
    private static String PASSWORD = "root";
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
    	try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        if(connection==null){
        	try {
        		connection = DriverManager.getConnection(DB_URL,USERNAME,PASSWORD);
        		System.out.println("connecting to the database: " + DB_URL);
        	} catch(SQLException se){
        		System.out.println("connecting DB problem: ");
                se.printStackTrace();
        	}
        } else {
        	System.out.println("connection is already on! ");
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
