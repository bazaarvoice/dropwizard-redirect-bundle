package com.bazaarvoice.dropwizard.redirect;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import org.junit.Test;
import org.mockito.internal.stubbing.answers.ThrowsException;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class HttpsRedirectTest {
    private static final String PUBLIC_IP = "1.2.3.4";
    private static final String LOOPBACK_IP = "127.0.0.1";
    private static final String SITE_LOCAL_IP = "10.100.1.1";
    private static final String LINK_LOCAL_IP = "169.254.0.1";
    private static final String SOURCE_SERVER = "source-server";
    private static final String TARGET_SERVER = "target-server";

    private final HttpsRedirect redirect = new HttpsRedirect();
    private final HttpsRedirect redirectMapper = new HttpsRedirect(true, new Function<String, String>() {
        @Override
        public String apply(String sourceName) {
            return SOURCE_SERVER.equals(sourceName) ? TARGET_SERVER : sourceName;
        }
    });

    @Test
    public void testPublicHttpsUrl() {
        HttpServletRequest request = request(PUBLIC_IP, "https://server/path");

        String url = redirect.getRedirect(request);
        assertNull(url);
    }

    @Test
    public void testLoopbackHttpsUrl() {
        HttpServletRequest request = request(LOOPBACK_IP, "https://server/path");

        String url = redirect.getRedirect(request);
        assertNull(url);
    }

    @Test
    public void testSiteLocalHttpsUrl() {
        HttpServletRequest request = request(SITE_LOCAL_IP, "https://server/path");

        String url = redirect.getRedirect(request);
        assertNull(url);
    }

    @Test
    public void testLinkLocalHttpsUrl() {
        HttpServletRequest request = request(LINK_LOCAL_IP, "https://server/path");

        String url = redirect.getRedirect(request);
        assertNull(url);
    }

    @Test
    public void testPublicHttpUrl() {
        HttpServletRequest request = request(PUBLIC_IP, "http://server/path");

        String url = redirect.getRedirect(request);
        assertEquals("https://server/path", url);
    }

    @Test
    public void testPublicHttpUrlWithParameters() {
        HttpServletRequest request = request(PUBLIC_IP, "http://server/path?key=value");

        String url = redirect.getRedirect(request);
        assertEquals("https://server/path?key=value", url);
    }

    @Test
    public void testPublicHttpUrlWithPort() {
        HttpServletRequest request = request(PUBLIC_IP, "http://server:8080/path");

        String url = redirect.getRedirect(request);
        assertEquals("https://server/path", url);
    }

    @Test
    public void testLoopbackHttpUrl() {
        HttpServletRequest request = request(LOOPBACK_IP, "http://server/path");

        String url = redirect.getRedirect(request);
        assertNull(url);
    }

    @Test
    public void testSiteLocalHttpUrl() {
        HttpServletRequest request = request(SITE_LOCAL_IP, "http://server/path");

        String url = redirect.getRedirect(request);
        assertNull(url);
    }

    @Test
    public void testLinkLocalHttpUrl() {
        HttpServletRequest request = request(LINK_LOCAL_IP, "http://server/path");

        String url = redirect.getRedirect(request);
        assertNull(url);
    }

    @Test
    public void testPublicHttpsRedirectUrl() {
        HttpServletRequest request = request(PUBLIC_IP, "https://source-server/path");

        String url = redirectMapper.getRedirect(request);
        assertNull(url);
    }

    @Test
    public void testLoopbackHttpsRedirectUrl() {
        HttpServletRequest request = request(LOOPBACK_IP, "https://source-server/path");

        String url = redirectMapper.getRedirect(request);
        assertNull(url);
    }

    @Test
    public void testSiteLocalHttpsRedirectUrl() {
        HttpServletRequest request = request(SITE_LOCAL_IP, "https://source-server/path");

        String url = redirectMapper.getRedirect(request);
        assertNull(url);
    }

    @Test
    public void testLinkLocalHttpsRedirectUrl() {
        HttpServletRequest request = request(LINK_LOCAL_IP, "https://source-server/path");

        String url = redirectMapper.getRedirect(request);
        assertNull(url);
    }

    @Test
    public void testPublicHttpRedirectUrl() {
        HttpServletRequest request = request(PUBLIC_IP, "http://source-server/path");

        String url = redirectMapper.getRedirect(request);
        assertEquals("https://target-server/path", url);
    }

    @Test
    public void testPublicHttpRedirectUrlWithParameters() {
        HttpServletRequest request = request(PUBLIC_IP, "http://source-server/path?key=value");

        String url = redirectMapper.getRedirect(request);
        assertEquals("https://target-server/path?key=value", url);
    }

    @Test
    public void testPublicHttpRedirectUrlWithPort() {
        HttpServletRequest request = request(PUBLIC_IP, "http://source-server:8080/path");

        String url = redirectMapper.getRedirect(request);
        assertEquals("https://target-server/path", url);
    }

    @Test
    public void testLoopbackRedirectHttpUrl() {
        HttpServletRequest request = request(LOOPBACK_IP, "http://source-server/path");

        String url = redirectMapper.getRedirect(request);
        assertNull(url);
    }

    @Test
    public void testSiteLocalRedirectHttpUrl() {
        HttpServletRequest request = request(SITE_LOCAL_IP, "http://source-server/path");

        String url = redirectMapper.getRedirect(request);
        assertNull(url);
    }

    @Test
    public void testLinkLocalRedirectHttpUrl() {
        HttpServletRequest request = request(LINK_LOCAL_IP, "http://source-server/path");

        String url = redirectMapper.getRedirect(request);
        assertNull(url);
    }

    @Test
    public void testPublicFtpUrl() {
        HttpServletRequest request = request(PUBLIC_IP, "ftp://server/path");

        String url = redirect.getRedirect(request);
        assertNull(url);
    }

    @Test
    public void testLoopbackFtpUrl() {
        HttpServletRequest request = request(LOOPBACK_IP, "ftp://server/path");

        String url = redirect.getRedirect(request);
        assertNull(url);
    }

    @Test
    public void testSiteLocalFtpUrl() {
        HttpServletRequest request = request(SITE_LOCAL_IP, "ftp://server/path");

        String url = redirect.getRedirect(request);
        assertNull(url);
    }

    @Test
    public void testLinkLocalFtpUrl() {
        HttpServletRequest request = request(LINK_LOCAL_IP, "ftp://server/path");

        String url = redirect.getRedirect(request);
        assertNull(url);
    }

    private static HttpServletRequest request(String sourceAddress, String url) {
        URL parsed;
        try {
            parsed = new URL(url);
        } catch (MalformedURLException e) {
            throw Throwables.propagate(e);
        }

        // By default have the mock throw an exception when we've forgotten to mock a method that is called.
        Exception exception = new RuntimeException("Forgot to mock a method");
        HttpServletRequest request = mock(HttpServletRequest.class, new ThrowsException(exception));

        doReturn(sourceAddress).when(request).getRemoteAddr();
        doReturn(parsed.getProtocol()).when(request).getScheme();
        doReturn(parsed.getHost()).when(request).getServerName();
        doReturn(new StringBuffer(url)).when(request).getRequestURL();
        doReturn(parsed.getPath()).when(request).getRequestURI();
        doReturn(parsed.getQuery()).when(request).getQueryString();
        return request;
    }
}