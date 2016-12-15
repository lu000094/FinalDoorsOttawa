package com.algonquinlive.lu000094.doorsopenottawa.model;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class mRequest {
    //Properties
    private String uri;
    private eHttpMethod method = eHttpMethod.GET;
    private Map<String, String> params = new HashMap<>();

    //Getters/Setters
    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    public eHttpMethod getMethod() {
        return method;
    }
    public void setMethod(eHttpMethod method) {
        this.method = method;
    }
    public Map<String, String> getParams() {
        return params;
    }
    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void setParam(String key, String value) {
        params.put(key, value);
    }

    //Convert params to encoded string
    public String encodedParams() {
        StringBuilder sb = new StringBuilder();
        for (String key : params.keySet()) {
            String value = null;
            try {
                value = URLEncoder.encode(params.get(key), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(key + "=" + value);
        }
        return sb.toString();
    }
}
