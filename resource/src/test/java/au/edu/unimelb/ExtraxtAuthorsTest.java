package au.edu.unimelb;

import au.edu.unimelb.sri.data.SqlCommand;
import au.edu.unimelb.sri.data.SqlConnection;
import au.edu.unimelb.sri.data.SqlParameter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sulman on 10/02/2017.
 */
public class ExtraxtAuthorsTest extends TestCase {

    ObjectMapper mapper = new ObjectMapper();

    /**
     * given an article_summary extract the list of authors
     */
    public void testAuthors() throws Exception {
//        String file = "src/test/resources/article-summary.json";
//        InputStream in = Files.newInputStream(new File(file).toPath());//rURL.openStream();
//
//        String query = "SELECT article_id_pk, article_id, article_summary from articles " +
//                "where article_id_pk!=49448 and first_author is null order by article_id_pk";

        String query = "SELECT article_id_pk, article_id, article_summary from articles " +
                " where article_id_pk!=49448";


        SqlConnection connection = new SqlConnection();
        SqlCommand command = new SqlCommand(query, connection.getDatabaseConnection());
        ResultSet resultSet = command.executeSelect();
        List<Article> idList = new ArrayList();
        //populate List::idList
        while (resultSet.next()) {
            int pk = resultSet.getInt("article_id_pk");
            int id = resultSet.getInt("article_id");
            String s = resultSet.getString("article_summary");
            idList.add(new Article(id, pk, s));
        }

        command.close();

        System.out.println("list size: " + idList.size());
        if(idList.size()<=0) {
            return;
        }

        int skipped = 0;
        List skipList = new ArrayList();
        for (int i = 0; i < idList.size(); i++) {
            Article article = idList.get(i);
            String summary = article.getArticleSummary();
            int pk = article.getArticleIdPK();
            int id = article.getArticleId();

            System.out.println("processing " + i + ". " + "uid: " + ", article_id_pk: " + pk + "" +
                    " , article_id : " + id);

            JsonNode root = mapper.readValue(summary, JsonNode.class);
            //System.out.println(root);
            int articleId;

            try {
                articleId = root.get("uid").asInt();
                String firstAuthor = root.get("sortfirstauthor").asText();
                if(firstAuthor.length()==0) {
                    String publisherName = root.get("publishername").asText();
               //     insertFirstAuthor("publisher_name",publisherName, articleId, pk);
                }else {
                    System.out.println(firstAuthor);
                 //   insertFirstAuthor("first_author",firstAuthor, articleId, pk);
                }

            } catch (NullPointerException e) {
                //e.printStackTrace();
                articleId = id;
                skipped++;
                System.out.println("pk: " + pk);
                skipList.add(article);
               // fixArticleSummary(pk);

                //  continue;
            }
            JsonNode nodeAuthors = root.withArray("authors");
            Iterator<JsonNode> authors = nodeAuthors.iterator();
            while (authors.hasNext()) {
                JsonNode author = authors.next();
                System.out.println(author);
                insertTOAuthors(author, articleId, pk);
            }
        //if(i==5)System.exit(1);
        }
        System.out.println("total skipped: " + skipped);

//        for (int k=0; k<skipList.size(); k++) {
//
//
//            fixArticleSummary();
//        }


        connection.close();
    }

    private void insertFirstAuthor(String col, String author, int articleId, int pk) throws SQLException {
        String query = "UPDATE articles SET "+ col +" = ?" +
                " WHERE article_id_pk = " + pk;
        SqlConnection connection = new SqlConnection();
        SqlCommand command = new SqlCommand(query, connection.getConnection());
        command.addParameter(new SqlParameter("first_author", author, Types.VARCHAR));
        int rowsEffected = command.execute();
        System.out.println(rowsEffected + " rows effected.");

        //command.close();
    }

    private void insertTOAuthors(JsonNode author, int articleId, int pk) throws SQLException {
        String name = author.get("name").asText();
        String authtype = author.get("authtype").asText();
        String clusterid = author.get("clusterid").asText();
        //System.out.println(name + " , " + authtype + " , " + clusterid);

        String query = "UPDATE "+Common.AUTHORS_TABLE_V2+" (author_name, authtype, clusterid, " +
                "ref_articles_article_id, fk_article_id_pk) " +
                "VALUES (?,?,?,?,?)";
        SqlConnection connection = new SqlConnection();
        SqlCommand command = new SqlCommand(query, connection.getDatabaseConnection());

        command.addParameter(new SqlParameter("author_name", name, Types.VARCHAR));
        command.addParameter(new SqlParameter("authtype", authtype, Types.VARCHAR));
        command.addParameter(new SqlParameter("clusterid", clusterid, Types.VARCHAR));
        //command.addParameter(new SqlParameter("ref_articles_article_id", articleId, Types.INTEGER));
        //command.addParameter(new SqlParameter("fk_article_id_pk", pk, Types.INTEGER));

        int rowsEffected = command.execute();
        System.out.println(rowsEffected + " rows effected.");
    }

    private void fixArticleSummary(int pk_param) throws Exception {
        String query = "SELECT article_id_pk, article_id, article_summary from articles" +
                " WHERE article_id_pk = " + pk_param;

        SqlConnection connection = new SqlConnection();
        SqlCommand command = new SqlCommand(query, connection.getConnection());
        ResultSet resultSet = command.executeSelect();
        List<Article> idList = new ArrayList();
        //populate List::idList
        while (resultSet.next()) {
            int pk = resultSet.getInt("article_id_pk");
            int id = resultSet.getInt("article_id");
            String s = resultSet.getString("article_summary");
            idList.add(new Article(id, pk, s));
        }
       // resultSet.close();
        //connection.close();

        System.out.println("list size: " + idList.size());

        for (int i = 0; i < idList.size(); i++) {
            Article article = idList.get(i);
            String summary = article.getArticleSummary();
            int pk = article.getArticleIdPK();
            int id = article.getArticleId();

            System.out.println("processing " + i + ". " + "uid: " + ", article_id_pk: " + pk + "" +
                    " , article_id : " + id);

            JsonNode root = mapper.readValue(summary, JsonNode.class);

            new Common().parseSummary(summary, id, pk);
        }
    }


    /**
     * Step-3: extract article metadata
     * @throws Exception
     */
    public void testFixArticleSummary() throws Exception {
        String query = "SELECT article_id_pk, article_id, article_summary from "+ Common.ARTICLES_TABLE_V2 ; // +
                //" WHERE article_id_pk!= 49448";

        SqlConnection connection = new SqlConnection();
        SqlCommand command = new SqlCommand(query, connection.getDatabaseConnection());
        ResultSet resultSet = command.executeSelect();
        List<Article> idList = new ArrayList();
        //populate List::idList
        while (resultSet.next()) {
            int pk = resultSet.getInt("article_id_pk");
            int id = resultSet.getInt("article_id");
            String s = resultSet.getString("article_summary");
            idList.add(new Article(id, pk, s));
        }
        resultSet.close();
        connection.close();

        System.out.println("list size: " + idList.size());

        for (int i = 0; i < idList.size(); i++) {
            Article article = idList.get(i);
            String summary = article.getArticleSummary();
            int pk = article.getArticleIdPK();
            int id = article.getArticleId();

            System.out.println("processing " + i + ". " + "uid: " + ", article_id_pk: " + pk + "" +
                    " , article_id : " + id);

            JsonNode root = mapper.readValue(summary, JsonNode.class);

            try {
                new Common().parseSummaryGetArticleTitle(summary, id, pk);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            //System.exit(1);
        }//end for
    }

    class Article {
        private int articleId;
        private int articleIdPK;
        private String articleSummary;


        public Article(int id, int pk, String summary) {
            articleId = id;
            articleIdPK = pk;
            articleSummary = summary;
        }

        public int getArticleId() {
            return articleId;
        }

        public int getArticleIdPK() {
            return articleIdPK;
        }

        public String getArticleSummary() {
            return articleSummary;
        }



    }
}
