package au.edu.unimelb;

import org.postgresql.util.PGobject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TermHitsList extends Base {

    private String term;
    private int hits;
    private List idList;


    /**
     * @param t    - searchTerm
     * @param list - idList
     * @param h    - hits
     */
    public TermHitsList(String t, List list, int h) {
        term = t;
        idList = list;
        hits = h;
    }

    /**
     * Get idList wrapped in PGObject
     *
     * @return PGobject
     */
    public PGobject getIdListAsPGObject() throws SQLException {
        PGobject jsonObject = new PGobject();
        jsonObject.setType("json");
        jsonObject.setValue(idList.toString());
        return jsonObject;
    }

    public String getTerm() {
        return term;
    }

    public List getIdList() {
        return idList;
    }

    public int getHits() {
        return hits;
    }


    /**
     * @return SQL Select Statement
     */
    @Override
    protected String getSqlForEntity() {

        String sql = "SELECT * from terms_idlist";//SQL.SELECT_ID_TERM_TERMS_IDLIST;
        return sql;
    }

    @Override
    protected String getInsertSqlForEntity() {
        //String sql = "INSERT INTO terms_idlist(term,hits) VALUES (\""+term+"\","+idList+")";
        String sql = "INSERT INTO terms_idlist(term,hits,idlist) VALUES (?,?,?)";
        return sql;
    }

    @Override
    protected String getUpdateSqlForEntity() {
        String sql = "UPDATE terms_idlist SET idlist = ?";
        return sql;
    }

    @Override
    protected String getDeleteSqlForEntity() {
        String sql = "";
        return sql;
    }


    @Override
    protected void addEntityToListFromResultSet(ResultSet rs) throws SQLException {
        int id_col = rs.getInt("id");
        String term_col = rs.getString("term");

        //TermHitsList p = new TermHitsList();
        //p.setName(rs.getString("name"));
        //p.setBrand(rs.getString("brand"));
        //partList.add(p);
    }

//    public void getIdList(){
//        //return (Part[]) partList.toArray(TEMPLATE);
//    }


}
