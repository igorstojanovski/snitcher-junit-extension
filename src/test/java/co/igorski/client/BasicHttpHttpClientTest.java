package co.igorski.client;

import co.igorski.exceptions.SnitcherException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by Igor Stojanovski.
 * Date: 11/14/2018
 * Time: 7:53 AM
 */
class BasicHttpHttpClientTest {

    @Test
    public void shouldHandleNullUrlWhenPosting() {

        BasicHttpHttpClient basicHttpHttpClient = new BasicHttpHttpClient();
        assertThrows(SnitcherException.class, () -> basicHttpHttpClient.post(null, ""));
    }

    @Test
    public void shouldHandleNullUrlWhenPostingForm() {

        BasicHttpHttpClient basicHttpHttpClient = new BasicHttpHttpClient();
        assertThrows(SnitcherException.class, () -> basicHttpHttpClient.postForm(null, new HashMap<>()));
    }
}

