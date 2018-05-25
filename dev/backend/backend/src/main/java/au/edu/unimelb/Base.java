package au.edu.unimelb;

import org.postgresql.util.PGobject;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Created by sulman on 31/01/2017.
 */
public abstract class Base {

    String DRIVER_CLASS = "org.postgresql.ds.PGSimpleDataSource";
    String DB_URL = "jdbc:postgresql://localhost/spinal_pubmed";
    String USERNAME = "postgres";
    String PASSWORD = "";
    int MAX_POOL_SIZE = 10;
    int MIN_IDLE = 1;
    boolean AUTO_COMMIT = false;
//Class.forName("org.postgresql.Driver");


    private DataSource dataSource = null;

    /**
     * @return java.sql.Connection
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    protected Connection getDatabaseConnection() throws ClassNotFoundException,
            SQLException {
        Connection c;
        //Class.forName(DRIVER_CLASS);
        c = DriverManager.getConnection(DB_URL, "", "");
        return c;
    }

    /**
     * Provide the custom SQL statement to be used for executing query.
     *
     * @return
     */
    abstract protected String getSqlForEntity();

    abstract protected String getInsertSqlForEntity();

    abstract protected String getUpdateSqlForEntity();

    abstract protected String getDeleteSqlForEntity();

    /**
     * Create a Statement using provided connection object;
     * execute the query and return the ResultSet
     *
     * @return java.sql.ResultSet
     *
     * @throws SQLException
     */
    protected ResultSet createResultSet() throws SQLException {
        Statement stmt = getDataSource().getConnection().createStatement();
        return stmt.executeQuery(getSqlForEntity());
    }

    /**
     * @throws SQLException
     */
    protected void insert(TermHitsList param) throws SQLException {
        String sql = getInsertSqlForEntity();

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, param.getTerm());
            stmt.setInt(2, param.getHits());

            PGobject jsonObject = new PGobject();
            jsonObject.setType("json");
            jsonObject.setValue(param.getIdList().toString());
            stmt.setObject(3, jsonObject);

            System.out.println("SQL: " + sql);
            stmt.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    abstract protected void addEntityToListFromResultSet(ResultSet rs) throws SQLException;

    public void populate() throws Exception {
        ResultSet rs = createResultSet();
        while (rs.next()) {
            addEntityToListFromResultSet(rs);
        }
    }

    /**
     * Method for creating the Data Source.
     *
     * @return DataSource
     */
    private DataSource getDataSource() {
        if (this.dataSource == null) {
//            final HikariConfig hikariConfig = new HikariConfig();
//
//            hikariConfig.setDriverClassName(DRIVER_CLASS);
//            hikariConfig.setJdbcUrl(DB_URL);
//            hikariConfig.setUsername(USERNAME);
//            hikariConfig.setPassword(PASSWORD);
//
//            hikariConfig.setMaximumPoolSize(MAX_POOL_SIZE);
//            hikariConfig.setAutoCommit(AUTO_COMMIT);
//           // hikariConfig.setLeakDetectionThreshold(2000);
//
//            hikariConfig.setMinimumIdle(MIN_IDLE);
//            hikariConfig.setConnectionTimeout(3000);
//            hikariConfig.setIdleTimeout(TimeUnit.SECONDS.toMillis(10));
//            hikariConfig.setValidationTimeout(TimeUnit.SECONDS.toMillis(2));
//
//            this.dataSource = new HikariDataSource(hikariConfig);
        }

        return this.dataSource;
    }

}
