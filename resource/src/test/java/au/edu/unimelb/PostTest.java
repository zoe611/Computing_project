package au.edu.unimelb;

import au.edu.unimelb.sri.data.SqlCommand;
import au.edu.unimelb.sri.data.SqlConnection;
import au.edu.unimelb.sri.net.BaseNetworkAccess;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sulman on 10/02/2017.
 */
public class PostTest extends TestCase {
    ObjectMapper mapper = new ObjectMapper();
    BaseNetworkAccess baseNet = new BaseNetworkAccess();
    Common common = new Common();


    /**
     * Step-2: use POST request to get article summaries from pubmed
     * @throws Exception
     */
    public void testEPost() throws Exception {


        String query = "SELECT DISTINCT article_id from "+ Common.ARTICLES_TABLE_V2 +" WHERE article_summary IS NULL";
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

        System.out.println("list size: "+idList.size());

        int start = 0;
        int end = 0;
        int pageSize = 500;
        do {
            start = end;
            if ((idList.size() - start) < pageSize) {
                pageSize = (idList.size() - start);
            }
            end = start + pageSize;
            List subList = idList.subList(start, end);

            //System.out.println(counter++ + " - " + subList);
            System.out.println(counter++);

            getSummaries(subList);
            // System.exit(1);
        } while (((idList.size() - start) > pageSize));


    }

    private void getSummaries(List idList) throws Exception {
        /** Convert the List to csv string*/
        String strIdList = idList.toString();
        String id_csv = strIdList.substring(1, strIdList.length());

        /** Make a POST request using the csv string*/
        String result = postIdList(id_csv);
        System.out.print(result);

        /** parse the POST response XML and extract query_key and WebEnv params*/
        Map<String, String> postResult = parsePostResult(result);

        /** Use query_key and WebEnv params to make a GET request*/
        URI uri = baseNet.buildSummaryRequestURI(postResult.get("query_key"), postResult.get("WebEnv"));
        result = baseNet.httpGet(uri);

        /** parse the GET result and update DB*/
        common.parseBatchSummary(result, idList);
    }

    private String postIdList(String idList) throws URISyntaxException {
        URI uri = baseNet.buildPostRequestURL();
        String result = baseNet.httpPost(uri, "application/x-www-form-urlencoded", idList);
        return result;
    }

    private Map parsePostResult(String result) throws Exception {
        DocumentBuilderFactory dbFactory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new ByteArrayInputStream(result.getBytes("utf-8")));
        doc.getDocumentElement().normalize();
        System.out.println("Root element :"
                + doc.getDocumentElement().getNodeName());
        String queryKey = doc.getElementsByTagName("QueryKey").item(0).getTextContent();
        String webEnv = doc.getElementsByTagName("WebEnv").item(0).getTextContent();

        Map<String, String> map = new HashMap<>();
        map.put("query_key", queryKey);
        map.put("WebEnv", webEnv);

        return map;
    }

    public void testGetSummary() throws Exception {
        String key = "1";
        String webEnv = "NCID_1_376125312_130.14.22.215_9001_1486685971_1045266988_0MetA0_S_MegaStore_F_1";
        BaseNetworkAccess baseNet = new BaseNetworkAccess();
        URI uri = baseNet.buildSummaryRequestURI(key, webEnv);
        String result = baseNet.httpGet(uri);
        System.out.println(result);

        //new Common().parseBatchSummary(result, );
    }
}

