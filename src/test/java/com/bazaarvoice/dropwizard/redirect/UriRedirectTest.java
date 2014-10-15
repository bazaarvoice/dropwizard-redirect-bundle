package com.bazaarvoice.dropwizard.redirect;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.mockito.internal.stubbing.answers.ThrowsException;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class UriRedirectTest {
    @Test
    public void testMatchingPath() {
        UriRedirect redirect = new UriRedirect("http://server/old", "http://server/new");
        HttpServletRequest request = request("http://server/old");

        String uri = redirect.getRedirect(request);
        assertEquals("http://server/new", uri);
    }

    @Test
    public void testNonMatchingPath() {
        UriRedirect redirect = new UriRedirect("http://server/old", "http://server/new");
        HttpServletRequest request = request("http://server/path");

        String uri = redirect.getRedirect(request);
        assertNull(uri);
    }

    @Test
    public void testChangeScheme() {
        UriRedirect redirect = new UriRedirect("http://(.*)", "https://$1");
        HttpServletRequest request = request("http://server/path");

        String uri = redirect.getRedirect(request);
        assertEquals("https://server/path", uri);
    }

    @Test
    public void testChangePath() {
        UriRedirect redirect = new UriRedirect("(.*)/welcome.html", "$1/index.html");
        HttpServletRequest request = request("http://server/product/welcome.html");

        String uri = redirect.getRedirect(request);
        assertEquals("http://server/product/index.html", uri);
    }

    @Test
    public void testKeepParameters() {
        UriRedirect redirect = new UriRedirect(".*\\?(.*)", "http://new-server/new-path?$1");
        HttpServletRequest request = request("http://server/path?param1=1&param2=2");

        String uri = redirect.getRedirect(request);
        assertEquals("http://new-server/new-path?param1=1&param2=2", uri);
    }

    @Test
    public void testMultiplePatterns() {
        UriRedirect redirect = new UriRedirect(ImmutableMap.<String, String>builder()
                .put("http://server1/path1", "http://server1/path2")
                .put("http://server2/path1", "http://server2/path2")
                .build());
        HttpServletRequest request = request("http://server2/path1");

        String uri = redirect.getRedirect(request);
        assertEquals("http://server2/path2", uri);
    }

    private static HttpServletRequest request(String uri) {
        URL url;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
            throw Throwables.propagate(e);
        }

        // By default have the mock throw an exception when we've forgotten to mock a method that is called.
        Exception exception = new RuntimeException("Forgot to mock a method");
        HttpServletRequest request = mock(HttpServletRequest.class, new ThrowsException(exception));
        doReturn(url.getPath()).when(request).getRequestURI();
        doReturn(url.getQuery()).when(request).getQueryString();
        doReturn(new StringBuffer(url.toExternalForm())).when(request).getRequestURL();
        return request;
    }
}
