package com.bazaarvoice.dropwizard.redirect;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Redirect {
    /**
     * Determine where to redirect the given request.  If no redirection should take place, then {@code null} should be
     * returned.
     */
    String getRedirect(HttpServletRequest request);
}
