package au.edu.unimelb;

import au.edu.unimelb.sri.data.SqlCommand;
import au.edu.unimelb.sri.data.SqlConnection;
import au.edu.unimelb.sri.data.SqlParameter;
import au.edu.unimelb.sri.net.BaseNetworkAccess;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {

    ObjectMapper mapper = new ObjectMapper();

    String ARTICLES_TABLE = "articlesv3";
    String IDLIST_TABLE = "terms_idlist";

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }



    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        String s = "a,b,c,";
        System.out.println(s);

        System.out.println(s.replaceAll(",$",""));
    }

    /**
     * Step-1: flatten the idList in terms_idList table into articles table
     *
     * @throws Exception
     */
    public void testSelect() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String query = "SELECT * FROM " + IDLIST_TABLE;
        SqlConnection connection = new SqlConnection();
        SqlCommand command = new SqlCommand(query, connection.getDatabaseConnection());
        ResultSet resultSet = command.executeSelect();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String term = resultSet.getString("term");
            System.out.println("id: " + id + ", term: " + term);

            JsonNode idListNode = mapper.convertValue(resultSet.getObject("idlist"), JsonNode.class);
            List idList = mapper.readValue(idListNode.get("value").asText(), List.class);
            System.out.println("idlist: " + idList);

            // System.out.println(idList);
            Iterator<Integer> iter = idList.iterator();
            while (iter.hasNext()) {
                Long docId = Long.valueOf(iter.next());
                System.out.println("idList: " + docId + "dfdfsdf");
                query = "INSERT INTO " +
                        ARTICLES_TABLE +
                        " (article_id,searchterm) VALUES (?,?)";
                command = new SqlCommand(query, connection.getDatabaseConnection());
                command.addParameter(new SqlParameter("article_id", docId, Types.BIGINT));
                command.addParameter(new SqlParameter("searchterm", term, Types.VARCHAR));

                int rowsEffected = command.execute();
                System.out.println(rowsEffected + " rows effected.");
            }
        }

        connection.close();
    }

    public void testInsertBatchArticleSummary() throws SQLException, URISyntaxException, IOException {
        BaseNetworkAccess baseNet = new BaseNetworkAccess();
        ObjectMapper mapper = new ObjectMapper();

        String query = "SELECT * from terms_idlist";
        SqlConnection connection = new SqlConnection();
        SqlCommand command = new SqlCommand(query, connection.getDatabaseConnection());
        ResultSet resultSet = command.executeSelect();
        int counter = 1;
        while (resultSet.next()) {

            int id_pk = resultSet.getInt("id");
            JsonNode idListNode = mapper.convertValue(resultSet.getObject("idlist"), JsonNode.class);
            List idList = mapper.readValue(idListNode.get("value").asText(), List.class);
            String id_csv = idList.toString();
            id_csv = id_csv.substring(1, id_csv.length());
            URI uri = baseNet.buildSummaryRequestURI(id_csv);
            String result = baseNet.httpGet(uri);

            query = "UPDATE terms_idlist SET article_summary_list = ? WHERE id = " + id_pk;
            command = new SqlCommand(query, connection.getDatabaseConnection());
            SqlParameter param = new SqlParameter("article_summary", SqlParameter.asPGObject(result),
                    Types.LONGVARCHAR);
            command.addParameter(param);

            int rowsEffected = command.execute();
            System.out.println(counter++ + " - " + rowsEffected + " rows effected.");
        }
    }


    public void testInsertBatch10ArticleSummary() throws Exception {

        String query = "SELECT DISTINCT article_id from articles WHERE article_summary IS NULL";
        SqlConnection connection = new SqlConnection();
        SqlCommand command = new SqlCommand(query, connection.getDatabaseConnection());
        ResultSet resultSet = command.executeSelect();
        int counter = 1;
        List idList = new ArrayList();
        //populate List::idList
        while (resultSet.next()) {
            idList.add(resultSet.getInt("article_id"));
        }
        resultSet.close();
        connection.close();

        int start = 0;
        int end = 0;
        int pageSize = 20;
        do {
            start = end;
            if ((idList.size() - start) < pageSize) {
                pageSize = (idList.size() - start);
            }
            end = start + pageSize;
            List subList = idList.subList(start, end);

            System.out.println(counter++ + " - " + subList);

            getSummaries(subList);
            //System.exit(1);
        } while (((idList.size() - start) > pageSize));
    }

    private void getSummaries(final List idList) throws URISyntaxException, IOException, SQLException {
        BaseNetworkAccess baseNet = new BaseNetworkAccess();
        ObjectMapper mapper = new ObjectMapper();

        String strIdList = idList.toString();
        String id_csv = strIdList.substring(1, strIdList.length());

        URI uri = baseNet.buildSummaryRequestURI(id_csv);
        String result = baseNet.httpGet(uri);
        //System.out.println(result);
        new Common().parseBatchSummary(result, idList);
//            URI uri = baseNet.buildSummaryRequestURI(id + "");
//            String result = baseNet.httpGet(uri);
//
//            query = "UPDATE articles SET article_summary = ? WHERE article_id = " + id;
//            command = new SqlCommand(query, connection.getDatabaseConnection());
//            SqlParameter param = new SqlParameter("article_summary", SqlParameter.asPGObject(result),
//                    Types.LONGVARCHAR);
//            command.addParameter(param);
//
//            int rowsEffected = command.execute();
//            System.out.println(counter++ + " - " + rowsEffected + " rows effected.");
    }


    public void testParseBatchSummary() throws IOException {
        String file = "src/test/resources/batch-summary.json";
        URL rURL = new File(file).toPath().toUri().toURL();
        InputStream in = rURL.openStream();
        JsonNode root = mapper.readValue(in, JsonNode.class);
        JsonNode resultNode = root.get("result").get("23423356");
        System.out.println(resultNode.toString());
        Iterator<Map.Entry<String, JsonNode>> iter = resultNode.fields();//iterator();
        while (iter.hasNext()) {
            Map.Entry<String, JsonNode> entry = iter.next();
            String key = entry.getKey();
            JsonNode node = entry.getValue();
            System.out.println(key + " : " + node);
        }
    }
}
