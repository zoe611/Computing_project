package au.edu.unimelb.sri.net;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sulman on 6/02/2017.
 */
public class BaseNetworkAccess {

    private String SCHEME = "https";
    private String HOST = "eutils.ncbi.nlm.nih.gov/entrez/eutils";
    private String SUMMARY_PATH = "/esummary.fcgi";
    private String EPOST_PATH = "/epost.fcgi";

    private String TOOL = "SRI_metadata_extraction";
    private String EMAIL = "muhammad.sarwar@unimelb.edu.au";

    public String httpGet(URI uri) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String responseBody = "";
        try {
            HttpGet httpget = new HttpGet(uri);
            System.out.println("Executing request " + httpget.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                //@Override
                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();

                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            responseBody = httpclient.execute(httpget, responseHandler);

            System.out.println("----------------------------------------");
            // System.out.println(responseBody);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return responseBody;
    }

    public InputStream httpGetReturnInputStream(URI uri) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        InputStream in = null;
        try {
            HttpGet httpget = new HttpGet(uri);
            System.out.println("Executing request " + httpget.getRequestLine());

            // Create a custom response handler
            ResponseHandler<InputStream> responseHandler = new ResponseHandler<InputStream>() {

                //@Override
                @Override
                public InputStream handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();

                        return entity != null ? entity.getContent() : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            in = httpclient.execute(httpget, responseHandler);

            System.out.println("----------------------------------------");
            // System.out.println(responseBody);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return in;
    }

    public String httpPost(URI uri, String contentType, String content) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String responseBody = "";
        try {
            HttpPost httpPost = new HttpPost(uri);
            System.out.println("Executing request " + httpPost.getRequestLine());

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("db", "pubmed"));
            nvps.add(new BasicNameValuePair("id", content));
            HttpEntity entity = new UrlEncodedFormEntity(nvps);
            httpPost.setHeader("Content-Type", contentType);
            httpPost.setEntity(entity);

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                //@Override
                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        HttpEntity entity = response.getEntity();
                        System.out.println(EntityUtils.toString(entity));
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            responseBody = httpclient.execute(httpPost, responseHandler);
            System.out.println("----------------------------------------");
            // System.out.println(responseBody);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return responseBody;
    }

    public URI buildPostRequestURL() throws URISyntaxException {
        return new URIBuilder()
                .setScheme(SCHEME)
                .setHost(HOST)
                .setPath(EPOST_PATH)
                .setParameter("tool", TOOL)
                .setParameter("email", EMAIL)
                .build();
    }

    public URI buildSummaryRequestURI(String key, String webEnv) throws URISyntaxException {
        return new URIBuilder()
                .setScheme(SCHEME)
                .setHost(HOST)
                .setPath(SUMMARY_PATH)
                .setParameter("query_key", key)
                .setParameter("WebEnv", webEnv)
                .setParameter("db", "pubmed")
                .setParameter("usehistory", "y")
                .setParameter("retmode", "json")
                .setParameter("tool", TOOL)
                .setParameter("email", EMAIL)
                .build();
    }

    /**
     * Build a URI for /eutils/esummary.fcgi
     *
     * @param id - single or list of csv
     *
     * @return
     *
     * @throws URISyntaxException
     */
    public URI buildSummaryRequestURI(String id) throws URISyntaxException {
        return new URIBuilder()
                .setScheme(SCHEME)
                .setHost(HOST)
                .setPath(SUMMARY_PATH)
                .setParameter("id", id)
                .setParameter("db", "pubmed")
                .setParameter("usehistory", "y")
                .setParameter("retmode", "json")
                .setParameter("tool", "SRI_metadata_extraction")
                .setParameter("email", "muhammad.sarwar@unimelb.edu.au")
                .build();
    }

}
