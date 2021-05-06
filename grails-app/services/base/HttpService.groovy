package base
import grails.converters.JSON
import groovy.json.JsonSlurper
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.grails.web.json.JSONElement
import org.springframework.http.MediaType
class HttpService {

    static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(HttpService)
    static okHttpClient=new OkHttpClient()
    static postJson(url, action, payload, customHeaders = null) {
        def paramsBody = [:]
        try {
            if (payload instanceof JSONElement || payload instanceof JSON) {
                paramsBody = new JsonSlurper().parseText(payload.toString())
            } else {
                paramsBody = payload
            }
        } catch (e) {
            logger.error("Failed to process payload: ${payload}", e)
            paramsBody = payload
        }
        def response = RestfulService.post(url, action) {
            contentType(MediaType.APPLICATION_JSON_VALUE)
            customHeaders?.each { k, v ->
                header(k, String.valueOf(v))
            }
            json(paramsBody)
        }
        if (response?.status != 200) {
            logger.error("Failed to request service ${url} with header ${customHeaders}, data ${payload}")
            throw new SystemException(response?.status ?: 500, response?.json?.code ?: 500, response?.json?.message)
        }
        return response.json
    }

    static postForm(url, action, formData, customHeaders = null) {
        def response = RestfulService.post(url, action) {
            contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            customHeaders?.each { k, v ->
                header(k, String.valueOf(v))
            }
            formData?.each { k, v ->
                setProperty(k, v)
            }
        }
        if (response?.status != 200) {
            logger.error("Failed to request service ${url} with header ${customHeaders}, data ${formData}")
            throw new SystemException(response?.status ?: 500, response?.json?.code ?: 500, response?.json?.message)
        }
        return response?.json
    }

    static postUrlencodedForm(url, action, formData, customHeaders = null) {
        def response = RestfulService.post(url, action) {
            contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            customHeaders?.each { k, v ->
                header(k, String.valueOf(v))
            }
            formData?.each { k, v ->
                setProperty(k, v)
            }
        }
        if (response?.status != 200) {
            logger.error("Failed to request service ${url} with header ${customHeaders}, data ${formData}")
            throw new SystemException(response?.status ?: 500, response?.json?.code ?: 500, response?.json?.message)
        }
        return response?.json
    }

    static get(url, action, params, customHeaders = null) {
        def response = RestfulService.get(url, action, params) {
            customHeaders?.each { k, v ->
                header(k, String.valueOf(v))
            }
        }
        if (response?.status != 200) {
            logger.error("Failed to request service ${url} with header ${customHeaders}")
            throw new SystemException(response?.status ?: 500, response?.json?.code ?: 500, response?.json?.message)
        }
        return response?.json
    }

    static postBasicForm(url, action, formData, customHeaders = null) {
        def response = RestfulService.post(url, action) {
            contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            customHeaders?.each { k, v ->
                header(k, String.valueOf(v))
            }
            formData?.each { k, v ->
                setProperty(k, v)
            }
        }
        return response
    }
    static QRCode(url, action, payload, customHeaders = null) {
        url=url+"/"+action
        def bodyStr=(payload as JSON).toString()
        RequestBody body = FormBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8")
                , bodyStr)
        def builder = new Request.Builder()
                .url(url)
                .post(body);
        if (customHeaders) {
            customHeaders.each {
                builder.addHeader(it.key, it.value)
            }
        }

        def request = builder.build()
        logger.debug("post msg: ${bodyStr} to ${url}")
        def response = okHttpClient.newCall(request).execute()
        def bytes=response.body().bytes()
        return bytes
    }
}
