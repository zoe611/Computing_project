package au.edu.unimelb.sri.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sulman on 7/02/2017.
 */
public class SqlCommand {

    private String command;
    private Connection connection;
    private List<SqlParameter> parameter;

    PreparedStatement stmt;

    public SqlCommand() {
        parameter = new ArrayList<>();
    }

    /**
     * Create an SqlCommand
     *
     * @param q - sql query
     * @param c - java.sql.Connection
     */
    public SqlCommand(String q, Connection c) {
        command = q;
        connection = c;
        parameter = new ArrayList<>();
    }


    /**
     * close ResultSet, PreparedStatement and Connection used by this SqlCommand.
     *
     * @throws SQLException
     */
    public void close() throws SQLException{
        if(!stmt.getResultSet().isClosed()) {
            stmt.getResultSet().close();
        }
        if(!stmt.isClosed()) {
            stmt.close();
        }
        if(!connection.isClosed()) {
            //connection.close();
        }
    }

    /**
     * execute SELECT sql statement and return the ResultSet
     *
     * @return java.sql.ResultSet
     */
    public ResultSet executeSelect() throws SQLException {
//        if (connection == null) {
//            connection = new SqlConnection().getDatabaseConnection();
//        }

        ResultSet resultSet = null;

        //try(
                stmt = connection.prepareStatement(command);
        //)
        {
            for (int i = 0; i < parameter.size(); i++) {
                SqlParameter param = parameter.get(i);
                String name = param.getParamName();
                int type = param.getParamType();
                Object val = param.getParamValue();
                stmt.setObject(i + 1, val);
            }
            resultSet = stmt.executeQuery();
        }
        return resultSet;
    }

    /**
     * add SqlParameter
     *
     * @param param
     */

    public void addParameter(SqlParameter param) {
        parameter.add(param);
    }

    /**
     * execute INSERT, UPDATE or DELETE sql statement and return number of rows effected.
     */
    public int execute() throws SQLException {

        if (connection == null) {
            connection = new SqlConnection().getDatabaseConnection();
        }
        int rowEffected = 0;
        try (PreparedStatement stmt = connection.prepareStatement(command)) {
            for (int i = 0; i < parameter.size(); i++) {
                SqlParameter param = parameter.get(i);
                Object val = param.getParamValue();
                String val_string = val.toString();
                stmt.setString(i + 1, val_string);
            }
            rowEffected = stmt.executeUpdate();

            //connection.commit();
            //stmt.close();
            //connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    //connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return rowEffected;
    }


    public String getCommand() {
        return command;
    }

    public void setCommand(final String sqlCommand) {
        this.command = sqlCommand;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(final Connection connection) {
        this.connection = connection;
    }

}
