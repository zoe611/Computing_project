package au.edu.unimelb;

import au.edu.unimelb.sri.data.SqlCommand;
import au.edu.unimelb.sri.data.SqlConnection;
import au.edu.unimelb.sri.data.SqlParameter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.*;

/**
 * Created by sulman on 22/04/2017.
 */
public class HitsIdListTest extends TestCase {

    String IDLIST_TABLE = "terms_idlistv3";
    String ARTICLES_TABLE = "articlesv3";
    String HITS_TABLE  = "hits_idlist";

    public void testSelect() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String query = "SELECT * FROM " + IDLIST_TABLE;
        SqlConnection connection = new SqlConnection();
        SqlCommand command = new SqlCommand(query, connection.getDatabaseConnection());
        ResultSet resultSet = command.executeSelect();
        List list = new ArrayList();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String term = resultSet.getString("term");
            int hits = resultSet.getInt("hits");
            System.out.println("id: " + id + ", term: " + term);

            Object idListObj = resultSet.getObject("idlist");

            JsonNode idListNode = mapper.convertValue(idListObj, JsonNode
                    .class);

            List idList = mapper.readValue(idListNode.get("value").asText(), List.class);
            Data data = new Data(term,hits,idList);
            list.add(data.toString());
        }//end while

        query = "INSERT INTO " +
                HITS_TABLE +
                " (data) VALUES (?)";
        command = new SqlCommand(query, connection.getDatabaseConnection());

        command.addParameter( new SqlParameter("data", SqlParameter.asPGObject(list),
                Types.LONGVARCHAR));

        int rowsEffected = command.execute();
        System.out.println(rowsEffected + " rows effected.");

        connection.close();

        //System.out.println(list);
    }

}

/**
 *
 */
class Data{
        String term;
        int hits;
        List idList;

        public Data(String term, int hits, List idList){
            this.term = term;
            this.hits = hits;
            this.idList = idList;
        }


        public String toString()  {
            String r = "";
            try {
                Map map = new HashMap<String,Object>();

                map.put("hits",this.hits);
                map.put("term",this.term);
                map.put("idlist",idList);
               r =  new ObjectMapper().writeValueAsString(map);
            }catch (Exception ex){
                ex.printStackTrace();
            }

            return r;
        }
}
