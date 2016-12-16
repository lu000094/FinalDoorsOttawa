package com.algonquinlive.lu000094.doorsopenottawa;

import android.os.Environment;
import android.util.Base64;

import com.algonquinlive.lu000094.doorsopenottawa.model.eHttpMethod;
import com.algonquinlive.lu000094.doorsopenottawa.model.mRequest;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

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
    public static String postDataWithParams(mRequest requestData) {

        BufferedReader reader = null;

        byte[] loginData = (userName + ":" + "password").getBytes();
        StringBuilder loginBuilder = new StringBuilder()
                .append("Basic ")
                .append(Base64.encodeToString(loginData, Base64.DEFAULT));

        String uri = requestData.getUri();

        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(requestData.getMethod().toString());
            conn.addRequestProperty("Authorization", loginBuilder.toString());
            conn.addRequestProperty("Accept", "application/json");
            conn.addRequestProperty("Content-Type", "application/json");

            conn.setDoOutput(true);
            conn.setDoInput(true);

            JSONObject json = new JSONObject(requestData.getParams());
            String params = json.toString();

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(params);
            writer.flush();
            writer.close();

            conn.connect();
            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            reader.close();
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
    public static String uploadFile(mRequest myRequest) {
        final String boundary;
        final String LINE_FEED = "\r\n";
        HttpURLConnection httpConn;
        String charset;
        OutputStream outputStream;
        PrintWriter writer;
        byte[] loginBytes = (userName + ":" + "password").getBytes();
        StringBuilder loginBuilder = new StringBuilder()
                .append("Basic ")
                .append(Base64.encodeToString(loginBytes, Base64.DEFAULT));

        try {
            boundary = "===" + System.currentTimeMillis() + "===";
            URL url = new URL(myRequest.getUri());
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true); // indicates POST method
            httpConn.setDoInput(true);
            httpConn.addRequestProperty("Authorization", loginBuilder.toString());
            httpConn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);
            httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
            outputStream = httpConn.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"),
                    true);
            String fileName = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() + myRequest.getImage().getName();
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append(
                    "Content-Disposition: form-data; name=\"" + "photoImage"
                            + "\"; filename=\"" + fileName + "\"")
                    .append(LINE_FEED);
            writer.append(
                    "Content-Type: "
                            + URLConnection.guessContentTypeFromName(fileName))
                    .append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);

            writer.append(LINE_FEED);
            writer.flush();


            FileInputStream inputStream = new FileInputStream(myRequest.getImage());
            byte[] buffer = new byte[6000];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();

            writer.append(LINE_FEED);
            writer.flush();

            List<String> response = new ArrayList<String>();

            writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();

            // checks server's status code first
            int status = httpConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        httpConn.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    response.add(line);
                }
                reader.close();
                httpConn.disconnect();
            } else {
                throw new IOException("Failed to request, status: " + status);
            }

            return response.toString();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
