package com.bazaarvoice.dropwizard.redirect;

import com.google.common.collect.ImmutableMap;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/** Redirects requests coming on a specific source path to a target path. */
public class PathRedirect implements Redirect {
    private final Map<String, String> pathMapping;
    private final boolean keepParameters;

    public PathRedirect(String sourceUri, String targetUri) {
        this(sourceUri, targetUri, true);
    }

    public PathRedirect(String sourceUri, String targetUri, boolean keepParameters) {
        checkNotNull(sourceUri);
        checkNotNull(targetUri);

        pathMapping = ImmutableMap.of(sourceUri, targetUri);
        this.keepParameters = keepParameters;
    }

    public PathRedirect(Map<String, String> uriMap) {
        this(uriMap, true);
    }

    public PathRedirect(Map<String, String> uriMap, boolean keepParameters) {
        checkNotNull(uriMap);

        pathMapping = ImmutableMap.copyOf(uriMap);
        this.keepParameters = keepParameters;
    }

    /** {@inheritDoc} */
    @Override
    public String getRedirect(HttpServletRequest request) {
        String uri = pathMapping.get(request.getRequestURI());
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