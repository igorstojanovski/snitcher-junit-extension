package co.igorski.client;

import java.io.IOException;
import java.util.Map;

public interface HttpClient {
    int postForm(String url, Map<String, String> form) throws IOException;
}
