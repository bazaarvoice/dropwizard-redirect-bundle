package com.bazaarvoice.dropwizard.redirect;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Redirects any non-HTTPS requests to the equivalent HTTPS URL.  This redirect is very simplistic and only relies on
 * what is actually contained in the request URL that the server sees.  It doesn't pay attention to any headers, and
 * specifically not the {@code X-Forwarded-Proto} or {@code X-Forwarded-Port} headers.  This is usually not an issue
 * because the default Dropwizard configuration has {@code http.useForwardedHeaders} set to {@code true}.  When that
 * property is set Dropwizard will configure Jetty to process any of the {@code X-Forwarded-*} headers prior to any
 * servlets being executed.  In general Jersey does the correct things with those headers and will update the request
 * appropriately.
 * <p/>
 * NOTE: If for some reason {@code http.useForwardedHeaders} is not set to {@code true} then this redirect may not
 * work as expected.
 */
public class HttpsRedirect implements Redirect {
    private final boolean allowPrivateIps;

    public HttpsRedirect() {
        this(true);
    }

    /**
     * @param allowPrivateIps If {@code true} then requests from private ip addresses won't be redirected.  An ip is
     *                        considered to be private if it is a loopback address, a link local address, or a site
     *                        local address.
     */
    public HttpsRedirect(boolean allowPrivateIps) {
        this.allowPrivateIps = allowPrivateIps;
    }

    @Override
    public String getRedirect(HttpServletRequest request) {
        if (allowPrivateIps && isPrivateIp(request.getRemoteAddr())) {
            return null;
        }

        String scheme = request.getScheme();
        if ("http".equals(scheme)) {
            return getRedirectUrl(request, "https");
        }

        return null;
    }

    /** Determine whether or not the provided address is private. */
    private boolean isPrivateIp(String address) {
        InetAddress ip;
        try {
            ip = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            return false;
        }

        return ip.isLoopbackAddress() || ip.isLinkLocalAddress() || ip.isSiteLocalAddress();
    }

    /** Return the full URL that should be redirected to including query parameters. */
    private String getRedirectUrl(HttpServletRequest request, String newScheme) {
        String serverName = request.getServerName();
        String uri = request.getRequestURI();
        String query = request.getQueryString();

        StringBuilder redirect = new StringBuilder(100);
        redirect.append(newScheme);
        redirect.append("://");
        redirect.append(serverName);
        redirect.append(uri);

        if (query != null) {
            redirect.append('?');
            redirect.append(query);
        }

        return redirect.toString();
    }
}
