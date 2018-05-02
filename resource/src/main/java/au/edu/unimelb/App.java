package au.edu.unimelb;

import au.edu.unimelb.sri.data.SqlCommand;
import au.edu.unimelb.sri.data.SqlConnection;
import au.edu.unimelb.sri.data.SqlParameter;
import au.edu.unimelb.sri.net.BaseNetworkAccess;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.ResponseHandler;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.params.ClientPNames;
//import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.utils.URIBuilder;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

/**
 * Extract article metadata from pubmed
 * <p>
 * #publications returned and #unique authors
 * <p>
 * extract the top 100 authors (=who published the most papers) from the data
 */
public class App {


    Scanner scanner;
    ObjectMapper mapper = new ObjectMapper();
    Map<String, String> searchResults = new LinkedHashMap<String, String>();

    BaseNetworkAccess baseNet = new BaseNetworkAccess();

    String IDLIST_TABLE = "terms_idlistv3";

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    /**
     * 1- calls search(term) and retrieves an idList
     * 2- stores the idList to DB
     */
    private void run() {
        String pathname = "src/main/resources/searchterms.txt";
        try {
            List<String> searchTerms = readInputFile(pathname);
            for (String term : searchTerms)
            {
                System.out.println(term);
                List idList = search(term);
                storeToDB(idList, term);
            }

            //buildSummaryRequestURI("28129443");


//            uri = app.buildCitationsRequestURI("28129443");



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * this function is used to get the result from the pubmed by searching one term
     * @param term
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    private List search(String term) throws URISyntaxException, IOException {
        List idList = new ArrayList();
        String result = baseNet.httpGet(buildSearchRequestURI(term, 1, 1));
        System.out.println("result for " + term + " is " + result);
        int hits = findTotalHits(result);
        idList = getIdList(term, hits);
        return idList;
    }


    /**
     * this function is used to count how many articles are derived by this term
     * @param result
     * @return total num of hits
     * @throws IOException
     */
    private int findTotalHits(String result) throws IOException {
        int hits = 0;
        JsonNode root = mapper.readValue(result, JsonNode.class);
        hits = root.get("esearchresult").get("count").asInt();
        System.out.println("total hits is: " + hits);
        return hits;
    }

    /**
     * 
     * @param term
     * @param hits
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    private List<String> getIdList(String term, int hits) throws IOException, URISyntaxException {
        List<String> idList = new ArrayList<String>();
        /**
         * pagination uses retstart and retmax params
         *
         * retmax = # of records returned
         *
         * retstart = start at specified recorod number
         *
         * ex: retmax=1000 & retstart=1
         * Start at record no 1 and return 1000 records
         *
         * retmax=1000 & retstart=1001
         * Start at record no 1001 and return next 1000 records
         *
         */
        int retmax = 1000;
        int retstart = 1;
        for (int i = 0; i < hits; i++) {
            String result = baseNet.httpGet(buildSearchRequestURI(term, retstart, retmax));
            JsonNode root = mapper.readValue(result, JsonNode.class);
            JsonNode listNode = root.get("esearchresult").withArray("idlist");

            List list = mapper.convertValue(listNode, List.class);

            idList.addAll(list);

            hits = hits - retmax;
            retstart = retmax + retstart;
        }
        System.out.println("idlist for " + term + " is " + idList);
        return idList;
    }



    /**
     * insert term, idList and number of hits to db
     *
     * @param idList
     * @param term
     *
     * @throws SQLException
     */
    private void storeToDB(List<String> idList, String term) throws SQLException {

        String query = "INSERT INTO "
                + IDLIST_TABLE +
                " (term,hits,idlist) " +
                "VALUES (?,?,?)";
        SqlConnection connection = new SqlConnection();
        SqlCommand command = new SqlCommand(query, connection.getDatabaseConnection());

        TermHitsList hitsList = new TermHitsList(term, idList, idList.size());

        command.addParameter(new SqlParameter("term", term, Types.VARCHAR));
        command.addParameter(new SqlParameter("hits", idList.size(), Types.INTEGER));
        command.addParameter(new SqlParameter("idList", hitsList.getIdListAsPGObject(), Types.LONGNVARCHAR));

        int rowsEffected = command.execute();

        System.out.println(rowsEffected + " rows inserted.");

    }



//    private void serializeSearchResultsMap() throws IOException {
//
//        mapper.writeValue(new File("src/main/resources/output.json"), searchResults);
//
//    }
//
//    private void processResult(String term, String result) throws IOException {
//        JsonNode root = mapper.readValue(result, JsonNode.class);
//
//        //searchResults.put(term,mapper.writeValueAsString(root.get("esearchresult").get("count").asInt()));
//
//        searchResults.put(term, root.toString());
//
//    }
//
//    /**
//     * $params{maxdate} = "2017/01/25";
//     * $params{email} = "sal7@mailinator.com";
//     * $params{db} = "pubmed";
//     * $params{mindate} = "2010/01/01";
//     * $params{tool} = "ebot";
//     * $params{datetype} = "PDAT";
//     * $params{term} = "spinal+cord+injury";
//     * %params = esearch(%params);
//     *
//     * @param term
//     *
//     * @return
//     *
//     * @throws URISyntaxException
//     */
//    private URI buildSearchRequestURI(String term) throws URISyntaxException {
//        return new URIBuilder()
//                .setScheme("https")
//                .setHost("eutils.ncbi.nlm.nih.gov/entrez")
//
//                .setPath("/eutils/esearch.fcgi")
//                .setParameter("term", term)
//                .setParameter("db", "pubmed")
//                .setParameter("usehistory", "y")
//                .setParameter("retmode", "json")
//                .setParameter("datetype", "pdat")
//                .setParameter("mindate", "2010/01/01")
//                .setParameter("maxdate", "2017/01/25")
//                .setParameter("retmax", "1000")
//                .setParameter("retstart", "1000")
//
//                .setParameter("email", "muhammad.sarwar@unimelb.edu.au")
//                .setParameter("tool", "SRI_metadata_extraction")
//                .build();
//    }

    private URI buildSearchRequestURI(String term, int retstart, int retmax) throws URISyntaxException {
        return new URIBuilder()
                .setScheme("https")
                .setHost("eutils.ncbi.nlm.nih.gov/entrez")

                .setPath("/eutils/esearch.fcgi")
                .setParameter("term", term)
                .setParameter("db", "pubmed")
                .setParameter("usehistory", "y")
                .setParameter("retmode", "json")
                .setParameter("datetype", "pdat")
                .setParameter("mindate", "2010/01/01")
                .setParameter("maxdate", "2016/12/25")
                .setParameter("retmax", Integer.toString(retmax))
                .setParameter("retstart", Integer.toString(retstart))

                .setParameter("email", "junwenz@unimelb.edu.au")
                .setParameter("tool", "SRI_metadata_extraction")
                .build();
    }

//    /**
//     * elink.fcgi
//     * <p>
//     * Build the URI to returns a list of articles in PMC that cite the given article in PubMed
//     *
//     * @param id - given id
//     *
//     * @return
//     *
//     * @throws URISyntaxException
//     */
//    private URI buildCitationsRequestURI(String id) throws URISyntaxException {
//        return new URIBuilder()
//                .setScheme("https")
//                .setHost("eutils.ncbi.nlm.nih.gov/entrez")
//                .setPath("/eutils/elink.fcgi")
//                .setParameter("dbfrom", "pubmed")
//                .setParameter("linkname", "pubmed_pubmed_citedin") //"pubmed_pmc_refs"
//                .setParameter("id", id)
//                .setParameter("usehistory", "n")
//                .setParameter("retmode", "json")
//                .setParameter("tool", "SRI_metadata_extraction")
//                .setParameter("email", "muhammad.sarwar@unimelb.edu.au")
//                .build();
//    }
//
//    
//    private URI buildSummaryRequestURI(String id) throws URISyntaxException {
//        return new URIBuilder()
//                .setScheme("https")
//                .setHost("eutils.ncbi.nlm.nih.gov/entrez")
//                .setPath("/eutils/esummary.fcgi")
//                .setParameter("id", id)
//                .setParameter("db", "pubmed")
//                .setParameter("usehistory", "n")
//                .setParameter("retmode", "json")
//                .setParameter("tool", "SRI_metadata_extraction")
//                .setParameter("email", "muhammad.sarwar@unimelb.edu.au")
//                .build();
//    }
//
//    private String getMetadataUsingEntrez(URI uri) {
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        String responseBody = "";
//        try {
//            HttpGet httpget = new HttpGet(uri);
//            System.out.println(httpget.getURI());
//
//            System.out.println("Executing request " + httpget.getRequestLine());
//
//            // Create a custom response handler
//            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
//
//                //@Override
//                @Override
//                public String handleResponse(
//                        final HttpResponse response) throws ClientProtocolException, IOException {
//                    int status = response.getStatusLine().getStatusCode();
//                    if (status >= 200 && status < 300) {
//                        HttpEntity entity = response.getEntity();
//                        return entity != null ? EntityUtils.toString(entity) : null;
//                    } else {
//                        throw new ClientProtocolException("Unexpected response status: " + status);
//                    }
//                }
//
//            };
//            responseBody = httpclient.execute(httpget, responseHandler);
//            System.out.println("----------------------------------------");
//            // System.out.println(responseBody);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                httpclient.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        return responseBody;
//    }
//
//    //28129443
//
//    /**
//     * @param id
//     */
//    private void getArticleSummaryEntrez(String id) {
//
//    }
//
//    private void getMetadataUsingPMCEurope(List searchTerms) {
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        try {
//
//            StringBuilder endpoint = new StringBuilder("http://www.ebi.ac.uk/europepmc/webservices/rest/search/query=");
//
//            endpoint.append(searchTerms.get(0).toString());
//
//            URI uri = new URIBuilder()
//                    .setScheme("http")
//                    .setHost("www.ebi.ac.uk/europepmc/webservices/rest")
//                    .setPath("/search")
//                    .setParameter("query", searchTerms.get(0).toString())
//                    .setParameter("resulttype", "idlist")
//                    .setParameter("format", "json")
//                    .setParameter("pubdate", "2011")
//                    .setParameter("src", "MED")
//                    .build();
//            HttpGet httpget = new HttpGet(uri);
//            System.out.println(httpget.getURI());
//
//            System.out.println("Executing request " + httpget.getRequestLine());
//
//            // Create a custom response handler
//            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
//
//                //@Override
//                @Override
//                public String handleResponse(
//                        final HttpResponse response) throws ClientProtocolException, IOException {
//                    int status = response.getStatusLine().getStatusCode();
//                    if (status >= 200 && status < 300) {
//                        HttpEntity entity = response.getEntity();
//                        return entity != null ? EntityUtils.toString(entity) : null;
//                    } else {
//                        throw new ClientProtocolException("Unexpected response status: " + status);
//                    }
//                }
//
//            };
//            String responseBody = httpclient.execute(httpget, responseHandler);
//            System.out.println("----------------------------------------");
//            System.out.println(responseBody);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                httpclient.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//    
    
    private List readInputFile(String pathname) throws FileNotFoundException {
        scanner = new Scanner(new File(pathname));
        List<String> searchTerms = new ArrayList<String>();

        while (scanner.hasNext()) {
            String term = scanner.nextLine();
            term = term.trim();
            if (!StringUtils.isBlank(term)) {
                searchTerms.add(term);
                System.out.println(term);
            }
        }

        System.out.println("Total search terms: " + searchTerms.size());

        return searchTerms;
    }
}
