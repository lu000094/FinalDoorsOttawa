package com.algonquinlive.lu000094.doorsopenottawa;

import android.util.Base64;

import com.algonquinlive.lu000094.doorsopenottawa.model.eHttpMethod;
import com.algonquinlive.lu000094.doorsopenottawa.model.mRequest;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Manage HTTP connections.
 *
 * Supported methods:
 * + getData() :: String
 *
 * @author David Gassner
 */

public class HttpManager {

    static String userName = "lu000094";
    /**
     * Return the HTTP response from uri
     * @param uri Uniform Resource Identifier
     * @return String the response; null when exception
     */
    public static String getData(String uri) {

        BufferedReader reader = null;

        try {
            // open the URI
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // make a buffered reader
            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            // read the HTTP response from URI one-line-at-a-time
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            // return the HTTP response
            return sb.toString();
            // exception handling: a) print stack-trace, b) return null
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }


    public static String getDataWithParams(mRequest requestData) {

        BufferedReader reader = null;

        byte[] loginData = (userName + ":" + "password").getBytes();
        StringBuilder loginBuilder = new StringBuilder()
                .append("Basic ")
                .append(Base64.encodeToString(loginData, Base64.DEFAULT));

        String uri = requestData.getUri();
        if (requestData.getMethod() == eHttpMethod.GET) {
            uri += "?" + requestData.encodedParams();
        }


        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(requestData.getMethod().toString());
            conn.addRequestProperty("Authorization", loginBuilder.toString());
            JSONObject json = new JSONObject(requestData.getParams());
            String params = json.toString();

            if (requestData.getMethod() == eHttpMethod.POST || requestData.getMethod() == eHttpMethod.PUT) {
                conn.addRequestProperty("Accept", "application/json");
                conn.addRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(params);
                writer.flush();
            }

            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }
}
