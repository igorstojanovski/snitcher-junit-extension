package co.igorski.client;

import co.igorski.exceptions.SnitcherException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

public interface HttpClient {

    int postForm(String url, Map<String, String> form) throws IOException, SnitcherException;

    String post(String url, String body) throws MalformedURLException, IOException, SnitcherException;
}
