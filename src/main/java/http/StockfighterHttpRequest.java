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
import java.util.stream.Collectors;

public abstract class StockfighterHttpRequest {
    private static final String apiKey;
    private static final ObjectMapper objectMapper = new ObjectMapper();

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
    private final HttpRequestType httpRequestType;
    private final BaseUrl baseUrl;
    private final String path;
    private final Map<String, Object> parameters;
    private final boolean requiresAuthorization;
    private final CloseableHttpClient httpClient;

    public StockfighterHttpRequest(final CloseableHttpClient httpClient,
                                   final HttpRequestType httpRequestType,
                                   final BaseUrl baseUrl, final String path,
                                   final boolean requiresAuthorization) {
        this.httpClient = httpClient;
        this.httpRequestType = httpRequestType;
        this.parameters = new HashMap<>();
        this.baseUrl = baseUrl;
        this.path = path;
        this.requiresAuthorization = requiresAuthorization;
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
        final HttpRequestBase httpRequestBase;
        final String url = baseUrl.url + path;
        switch (httpRequestType) {
            case GET:
                httpRequestBase = new HttpGet(url);
                break;
            case POST:
                final HttpPost httpPost = new HttpPost(url);

                // Construct JSON POST body
                if (!parameters.isEmpty()) {
                    final StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append('{');
                    final String collect = parameters.entrySet()
                            .parallelStream().map(entry -> {
                                final StringBuilder builder =
                                        new StringBuilder();
                                builder.append('"').append(entry.getKey())
                                        .append("\":");
                                Object value = entry.getValue();
                                if (value instanceof Integer) {
                                    builder.append(value);
                                } else {
                                    builder.append('"').append(value)
                                            .append('"');
                                }
                                return builder.toString();
                            }).collect(Collectors.joining(","));
                    stringBuilder.append(collect).append('}');
                    try {
                        final StringEntity postBody =
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
        try (final CloseableHttpResponse response =
                     httpClient.execute(httpRequestBase)) {
            // Based off of an example from
            // https://hc.apache.org/httpcomponents-client-ga/examples.html
            final StatusLine statusLine = response.getStatusLine();
            final int statusCode = statusLine.getStatusCode();
            if (statusCode >= 300) {
                logger.error("{}: {}", statusCode,
                        statusLine.getReasonPhrase());
                return null; // TODO: better idea?
            }

            if (statusCode != 200) {
                logger.warn("status  code: {}", statusCode);
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                return objectMapper.readValue(entity.getContent(),
                        getResponseClass());
            } else {
                logger.error("Response entity was null");
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

        private final String url;

        BaseUrl(final String url) {
            this.url = url;
        }
    }
}
