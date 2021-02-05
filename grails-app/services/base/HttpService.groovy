package base
import grails.converters.JSON
import groovy.json.JsonSlurper
import org.grails.web.json.JSONElement
import org.springframework.http.MediaType

class HttpService {

    static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(HttpService)

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
            contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            customHeaders?.each { k, v ->
                header(k, String.valueOf(v))
            }
            json(paramsBody)
        }
        if (response?.status != 200) {
            logger.error("Failed to request service ${url} with header ${customHeaders}, data ${payload}")
            throw new SystemException(response?.status ?: 500, response?.json?.code ?: 500, response?.json?.message)
        }
        return response?.json
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

}
