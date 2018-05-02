package au.edu.unimelb;

import au.edu.unimelb.sri.data.SqlCommand;
import au.edu.unimelb.sri.data.SqlConnection;
import au.edu.unimelb.sri.data.SqlParameter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sulman on 10/02/2017.
 */
public class Common {

    public static String ARTICLES_TABLE = "articles";
    public static String TERMS_IDLIST_TABLE = "terms_idlist";
    public static String AUTHORS_TABLE = "authors";

    public static String ARTICLES_TABLE_V2 = "articlesv3";
    public static String TERMS_IDLIST_TABLE_V2 = "terms_idlistv3";
    public static String AUTHORS_TABLE_V2 = "authorsv3";


    ObjectMapper mapper = new ObjectMapper();

    public void parseBatchSummary(String batchSummary, List idList) throws IOException, SQLException {
        JsonNode root = mapper.readValue(batchSummary, JsonNode.class);
        JsonNode resultNode = root.get("result");
        //Iterator<Map.Entry<String, JsonNode>> iter = resultNode.fields();
        int i = 0;
        for (i = 0; i < idList.size(); i++) {
            int article_id = (int) idList.get(i);
            JsonNode summaryNode = resultNode.get(idList.get(i).toString());
            //System.out.println(i + " - " + summaryNode);
            System.out.println(i + ". ");
            updateDB(article_id, summaryNode);

        }
        System.out.println("processed " + i + " records");

    }
    public void updateDB(int article_id, JsonNode summary) throws SQLException {
        String query = "UPDATE " + ARTICLES_TABLE_V2 + " articles SET article_summary = ? WHERE article_id = " + article_id;
        SqlConnection connection = new SqlConnection();
        SqlCommand command = new SqlCommand(query, connection.getDatabaseConnection());
        SqlParameter param = new SqlParameter("article_summary", SqlParameter.asPGObject(summary),
                Types.LONGVARCHAR);
        command.addParameter(param);

        int rowsEffected = command.execute();
        //System.out.println(rowsEffected + " rows effected.");
    }

    public void parseSummary(String batchSummary, int id, int pk) throws IOException, SQLException {
        JsonNode root = mapper.readValue(batchSummary, JsonNode.class);
        JsonNode resultNode = root.get("result");

        JsonNode summaryNode = resultNode.get(id + "");//string lookup (not int)
        System.out.println(summaryNode);

        String articleTitle = summaryNode.get("title").asText();
        String source = summaryNode.get("source").asText();
        updateDB(pk, id, summaryNode);

    }


    public void parseSummaryGetArticleTitle(String batchSummary, int id, int pk) throws IOException, SQLException {
        JsonNode summaryNode = mapper.readValue(batchSummary, JsonNode.class);

        //JsonNode summaryNode = root.get(id + "");//string lookup (not int)
        System.out.println(summaryNode);

        String articleTitle = summaryNode.get("title").asText();



        String source = summaryNode.get("source").asText();

        updateDB(pk, id, summaryNode);

    }


    /**
     * update article_summary
     *
     * @param pk
     * @param article_id
     * @param summary
     * @throws SQLException
     */
//    public void updateDB(int pk, int article_id, JsonNode summary) throws SQLException {
//        String query = "UPDATE "+Common.ARTICLES_TABLE_V2+" SET article_summary = ? WHERE article_id_pk = " + pk;
//        SqlConnection connection = new SqlConnection();
//        SqlCommand command = new SqlCommand(query, connection.getDatabaseConnection());
//        SqlParameter param = new SqlParameter("article_summary", SqlParameter.asPGObject(summary),
//                Types.LONGVARCHAR);
//        command.addParameter(param);
//
//        int rowsEffected = command.execute();
//        System.out.println(rowsEffected + " rows effected.");
//
//    }

    /**
     * update article_title
     *
     * @param pk
     * @param articleId
     * @param summary
     * @throws SQLException
     */
    public void updateDB(int pk, int articleId, JsonNode summary) throws SQLException {
        String query = "UPDATE "+Common.ARTICLES_TABLE_V2+" SET article_title = ? , source = ? ," +
                " sort_title = ?, pub_type = ? , elocation_id = ? , pub_date = ? , first_author = ? , " +
                " author_name = ? " +
                " WHERE article_id_pk = " + pk;
        SqlConnection connection = new SqlConnection();
        SqlCommand command = new SqlCommand(query, connection.getDatabaseConnection());

        String articleTitle = summary.get("title").asText();
        String source = summary.get("source").asText();

        SqlParameter param1 = new SqlParameter("article_title", articleTitle,
                Types.VARCHAR);
        SqlParameter param2= new SqlParameter("source", source,
                Types.VARCHAR);

        String sortTitle = summary.get("sorttitle").asText();
        SqlParameter param3= new SqlParameter("sort_title", sortTitle,
                Types.VARCHAR);

        String pubtypeNode = summary.withArray("pubtype").toString();
        if(pubtypeNode.length()>2) {
            pubtypeNode = pubtypeNode.substring(2, pubtypeNode.length() - 2);
            System.out.println("pubtype: " + pubtypeNode);
        }
        SqlParameter param4= new SqlParameter("pub_type", pubtypeNode,
                Types.VARCHAR);

        String eLocationId = summary.get("elocationid").asText();
        SqlParameter param5= new SqlParameter("elocation_id", eLocationId,
                Types.VARCHAR);


        String pubDate = summary.get("pubdate").asText();
        SqlParameter param6= new SqlParameter("pub_date", pubDate,
                Types.VARCHAR);

        String firstAuthor = summary.get("sortfirstauthor").asText();
        SqlParameter param7= new SqlParameter("first_author", firstAuthor,
                Types.VARCHAR);


        JsonNode nodeAuthors = summary.withArray("authors");
        Iterator<JsonNode> authors = nodeAuthors.iterator();
        StringBuilder auth = new StringBuilder();
        while (authors.hasNext()) {
            JsonNode author = authors.next();
            System.out.println(author);
            auth.append(author+",");
         insertTOAuthors(author, articleId, pk);
        }
        String auths = "["+auth.toString().replaceAll(",$","")+"]";
        SqlParameter param8= new SqlParameter("author_name", auths,
                Types.VARCHAR);

        command.addParameter(param1);
        command.addParameter(param2);
        command.addParameter(param3);
        command.addParameter(param4);
        command.addParameter(param5);
        command.addParameter(param6);
        command.addParameter(param7);
        command.addParameter(param8);


        int rowsEffected = command.execute();
        System.out.println(rowsEffected + " rows effected.");

    }


    private void insertTOAuthors(JsonNode author, int articleId, int pk) throws SQLException {
        String name = author.get("name").asText();
        String authtype = author.get("authtype").asText();
        String clusterid = author.get("clusterid").asText();
        //System.out.println(name + " , " + authtype + " , " + clusterid);

        String query = "INSERT INTO "+Common.AUTHORS_TABLE_V2+" (author_name, authtype, clusterid, " +
                "ref_articles_article_id, fk_article_id_pk) " +
                "VALUES (?,?,?,?,?)";
        SqlConnection connection = new SqlConnection();
        SqlCommand command = new SqlCommand(query, connection.getDatabaseConnection());

        command.addParameter(new SqlParameter("author_name", name, Types.VARCHAR));
        command.addParameter(new SqlParameter("authtype", authtype, Types.VARCHAR));
        command.addParameter(new SqlParameter("clusterid", clusterid, Types.VARCHAR));
        command.addParameter(new SqlParameter("ref_articles_article_id", articleId, Types.INTEGER));
        command.addParameter(new SqlParameter("fk_article_id_pk", pk, Types.INTEGER));

        int rowsEffected = command.execute();
        System.out.println(rowsEffected + " rows effected.");
    }
}
