package com.bazaarvoice.dropwizard.redirect;

import com.google.common.collect.ImmutableMap;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/** Redirects requests coming on a specific source URI to a target URI. */
public class UriRedirect implements Redirect {
    private final Map<String, String> uriMapping;
    private final boolean keepParameters;

    public UriRedirect(String sourceUri, String targetUri) {
        this(sourceUri, targetUri, true);
    }

    public UriRedirect(String sourceUri, String targetUri, boolean keepParameters) {
        checkNotNull(sourceUri);
        checkNotNull(targetUri);

        uriMapping = ImmutableMap.of(sourceUri, targetUri);
        this.keepParameters = keepParameters;
    }

    public UriRedirect(Map<String, String> uriMap) {
        this(uriMap, true);
    }

    public UriRedirect(Map<String, String> uriMap, boolean keepParameters) {
        checkNotNull(uriMap);

        uriMapping = ImmutableMap.copyOf(uriMap);
        this.keepParameters = keepParameters;
    }

    /** {@inheritDoc} */
    @Override
    public String getRedirect(HttpServletRequest request) {
        String uri = uriMapping.get(request.getRequestURI());
        if (uri == null) {
            return null;
        }

        StringBuilder redirect = new StringBuilder(uri);
        if (keepParameters) {
            String query = request.getQueryString();
            if (query != null) {
                redirect.append('?');
                redirect.append(query);
            }
        }

        return redirect.toString();
    }
}