package co.igorski.client;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.StringJoiner;

/**
 * This implementation uses only Java native classes to implement the {@link HttpClient} interface.
 */
public class BasicHttpHttpClient implements HttpClient {

    @Override
    public int postForm(String target, Map<String, String> form) throws IOException {
        URL url;
        url = new URL(target);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        try (OutputStream os = conn.getOutputStream();
             BufferedWriter writer = new BufferedWriter(
                     new OutputStreamWriter(os, "UTF-8"))) {
            writer.write(getPostDataString(form));
            writer.flush();
        }

        return conn.getResponseCode();
    }

    @Override
    public String post(String target, String body) throws IOException {

        URL url;
        url = new URL(target);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = conn.getOutputStream(); BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"))) {
            writer.write(body);
            writer.flush();
        }

        return getStringFromInputStream(conn.getInputStream());
    }

    private String getStringFromInputStream(InputStream inputStream) {

        String response = null;
        try (InputStream in = inputStream; ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            response = result.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    private String getPostDataString(Map<String, String> params) throws UnsupportedEncodingException {
        StringJoiner result = new StringJoiner("&");

        for (Map.Entry<String, String> entry : params.entrySet()) {

            result.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" +
                    URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
