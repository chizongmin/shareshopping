package base
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import org.apache.http.NoHttpResponseException
import org.apache.http.client.HttpClient
import org.apache.http.client.HttpRequestRetryHandler
import org.apache.http.client.config.RequestConfig
import org.apache.http.config.Registry
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.ConnectTimeoutException
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.socket.PlainConnectionSocketFactory
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.protocol.HttpContext
import org.grails.web.json.JSONObject
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate

import java.util.concurrent.TimeUnit

class RestfulService {

    static HttpClient httpClient() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build()
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry)
        connectionManager.setMaxTotal(100)
        connectionManager.setDefaultMaxPerRoute(10)
        connectionManager.setValidateAfterInactivity(5000)
        connectionManager.closeIdleConnections(6, TimeUnit.SECONDS)
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(5000).setConnectionRequestTimeout(6000).build()
        return HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setConnectionManager(connectionManager).setRetryHandler(new HttpRequestRetryHandler() {
            @Override
            boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount > 3) {
                    logger.warn("Maximum tries reached for client http pool ")
                    return false
                }
                if (exception instanceof NoHttpResponseException || exception instanceof ConnectTimeoutException) {
                    logger.warn("NoHttpResponseException|ConnectTimeoutException on " + executionCount + " call")
                    return true
                }
                return false
            }
        }).build()
    }

    static RestBuilder restBuilder
    static {
        def httpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient())
        httpRequestFactory.setConnectTimeout(180000)
        httpRequestFactory.setReadTimeout(180000)
        restBuilder = new RestBuilder(new RestTemplate(httpRequestFactory))
    }

    static Logger logger = org.slf4j.LoggerFactory.getLogger(RestfulService)

    static get(String url, String action, params = null, Closure body = null) {
        invokeApi(processGetParams(action ? [url, action].join("/") : url, params), "get", body)
    }

    static post(String url, String action = null, Closure body = null) {
        invokeApi(action ? [url, action].join("/") : url, "post", body)
    }

    static put(String url, String action, Closure body = null) {
        invokeApi(action ? [url, action].join("/") : url, "put", body)
    }

    private static processGetParams(String baseUrl, params = null) {
        def finalUrl = new StringBuffer(baseUrl)
        if (params) {
            def paramsContent = params.findAll { k, v -> v }.collect { k, v -> "${k}=${v}" }.join("&")
            if (paramsContent) {
                finalUrl.append("?")
                finalUrl.append(paramsContent)
            }
        }
        finalUrl.toString()
    }

    static invokeApi(String url, String method, Closure body = null) {
        logger.debug("send request ${method} to ${url}")
        RestResponse response
        try {
            response = restBuilder."${method}"(url, body)
            HttpStatus status = response.responseEntity.statusCode
            if (status != HttpStatus.OK) {
                logger.warn("${url} response is ${status}, not ${HttpStatus.OK}")
                if (!response.json) {
                    response.$json = new JSONObject([success: false, message: "Internal Error"])
                }
            }
        } catch (ResourceAccessException | ConnectException e) {
            throw e
        }
        response
    }

}
