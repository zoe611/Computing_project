package au.edu.unimelb.sri.data;

import org.postgresql.util.PGobject;

import java.sql.SQLException;

public class SqlParameter {

    private String paramName;
    private Object paramValue;
    private int paramType;


    /**
     * Create an SqlParameter
     *
     * @param name  - parameter name
     * @param value - parameter value
     * @param type  - java.sql.Types
     */
    public SqlParameter(String name, Object value, int type) {
        this.paramName = name;
        this.paramValue = value;
        this.paramType = type;
    }

    /**
     * Wrap the 'input' into a PGObject object and return wrapped PGObject
     *
     * @param input
     *
     * @return
     *
     * @throws SQLException
     */
    public static PGobject asPGObject(Object input) throws SQLException {
        PGobject jsonObject = new PGobject();
        jsonObject.setType("json");
        jsonObject.setValue(input.toString());
        return jsonObject;
    }

    public int getParamType() {
        return paramType;
    }

    public void setParamType(int paramType) {
        this.paramType = paramType;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public Object getParamValue() {
        return paramValue;
    }

    public void setParamValue(Object paramValue) {
        this.paramValue = paramValue;
    }

}
