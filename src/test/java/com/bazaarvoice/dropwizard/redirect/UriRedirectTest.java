package com.bazaarvoice.dropwizard.redirect;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.mockito.internal.stubbing.answers.ThrowsException;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class UriRedirectTest {
    private final UriRedirect redirect = new UriRedirect(ImmutableMap.<String, String>builder()
            .put("/old1", "/new1")
            .put("/old2", "/new2")
            .build()
    );

    @Test
    public void testMatchingUri() {
        HttpServletRequest request = request("/old1");

        String uri = redirect.getRedirect(request);
        assertEquals("/new1", uri);
    }

    @Test
    public void testMatchingUriWithKeepParameters() {
        HttpServletRequest request = request("/old1?key=value");

        String uri = redirect.getRedirect(request);
        assertEquals("/new1?key=value", uri);
    }

    @Test
    public void testMatchingUriWithoutKeepParameters() {
        UriRedirect redirect = new UriRedirect("/old1", "/new1", false);
        HttpServletRequest request = request("/old1?key=value");

        String uri = redirect.getRedirect(request);
        assertEquals("/new1", uri);
    }

    @Test
    public void testNonMatchingUri() {
        HttpServletRequest request = request("/new");

        String uri = redirect.getRedirect(request);
        assertNull(uri);
    }

    @Test
    public void testSubstringUri() {
        HttpServletRequest request = request("/old100");

        String uri = redirect.getRedirect(request);
        assertNull(uri);
    }

    private static HttpServletRequest request(String uri) {
        int question = uri.indexOf('?');
        String path = (question == -1) ? uri : uri.substring(0, question);
        String params = (question == -1) ? null : uri.substring(question + 1);

        // By default have the mock throw an exception when we've forgotten to mock a method that is called.
        Exception exception = new RuntimeException("Forgot to mock a method");
        HttpServletRequest request = mock(HttpServletRequest.class, new ThrowsException(exception));
        doReturn(path).when(request).getRequestURI();
        doReturn(params).when(request).getQueryString();
        return request;
    }
}