package http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class StockfighterHttpRequest {
    private static final String apiKey;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static CloseableHttpClient httpClient;

    static {
        Properties properties = new Properties();
        String propertiesFilename = "config.properties";

        try (InputStream inputStream = StockfighterHttpRequest.class.
                getClassLoader().getResourceAsStream(propertiesFilename)) {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new FileNotFoundException("Property file '" +
                        propertiesFilename + "' not found in resources folder");
            }
        } catch (Exception e) {
            Logger logger =
                    LoggerFactory.getLogger(StockfighterHttpRequest.class);
            logger.error("Property file error: ", e);
        }

        apiKey = properties.getProperty("apiKey");
    }

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private HttpRequestType httpRequestType;
    private BaseUrl baseUrl;
    private String path;
    private Map<String, Object> parameters;
    private boolean requiresAuthorization;

    public StockfighterHttpRequest(final HttpRequestType httpRequestType,
                                   final BaseUrl baseUrl, final String path,
                                   final boolean requiresAuthorization) {
        this.httpRequestType = httpRequestType;
        this.parameters = new HashMap<>();
        this.baseUrl = baseUrl;
        this.path = path;
        this.requiresAuthorization = requiresAuthorization;
    }

    // Must call this method prior to using subclasses
    public static void setHttpClient(final CloseableHttpClient httpClient) {
        StockfighterHttpRequest.httpClient = httpClient;
    }

    protected abstract Class<? extends StockfighterHttpResponse>
    getResponseClass();

    protected void addParameter(final String key, final Object value) {
        parameters.put(key, value);
    }

    public StockfighterHttpResponse getResponse() {
        if (httpClient == null) {
            logger.error("HTTP client is null.");
            return null; // TODO: better idea?
        }
        HttpRequestBase httpRequestBase;
        String url = baseUrl.url + path;
        switch (httpRequestType) {
            case GET:
                httpRequestBase = new HttpGet(url);
                break;
            case POST:
                HttpPost httpPost = new HttpPost(url);

                // Construct JSON POST body
                if (!parameters.isEmpty()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append('{');
                    boolean first = true;
                    for (Map.Entry<String, Object> keyValuePair :
                            parameters.entrySet()) {
                        if (first) {
                            first = false;
                        } else {
                            stringBuilder.append(',');
                        }
                        stringBuilder.append('"').append(keyValuePair.getKey())
                                .append("\":");
                        Object value = keyValuePair.getValue();
                        if (value instanceof Integer) {
                            stringBuilder.append(value);
                        } else {
                            stringBuilder.append('"').append(value).append('"');
                        }
                    }
                    stringBuilder.append('}');
                    try {
                        StringEntity postBody =
                                new StringEntity(stringBuilder.toString());
                        postBody.setContentType("application/json");
                        httpPost.setEntity(postBody);
                    } catch (UnsupportedEncodingException e) {
                        logger.error("Failed to set entity.", e);
                    }
                }
                httpRequestBase = httpPost;
                break;
            case DELETE:
                httpRequestBase = new HttpDelete(url);
                break;
            default:
                throw new RuntimeException("Unknown HTTP Request type");
        }

        logger.debug("{} {}", httpRequestType.toString(), url);
        if (requiresAuthorization) {
            httpRequestBase.addHeader("X-Starfighter-Authorization", apiKey);
            logger.debug("Adding API key");
        }
        try (CloseableHttpResponse response =
                     httpClient.execute(httpRequestBase)) {
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode >= 300) {
                logger.error("{}: {}", statusCode,
                        statusLine.getReasonPhrase());
                return null; // TODO: better idea?
            }

            if (statusCode != 200) {
                logger.warn("status  code: {}", statusCode);
            }

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return objectMapper.readValue(entity.getContent(),
                        getResponseClass());
            } else {
                logger.error("Response entity was null");
                return null; // TODO: better idea?
            }
        } catch (Exception e) {
            logger.error("Error in HTTP response. ", e);
        }
        return null; // TODO: better idea?
    }

    public enum HttpRequestType {
        GET, POST, DELETE
    }

    public enum BaseUrl {
        API("https://api.stockfighter.io/ob/api/"),
        GM("https://www.stockfighter.io/gm/");

        private String url;

        BaseUrl(String url) {
            this.url = url;
        }
    }
}
