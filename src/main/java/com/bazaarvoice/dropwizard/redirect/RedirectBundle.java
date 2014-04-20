package com.bazaarvoice.dropwizard.redirect;

import com.google.common.collect.Lists;
import com.google.common.net.HttpHeaders;
import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class RedirectBundle implements Bundle {
    private final List<Redirect> redirects = Lists.newArrayList();

    public RedirectBundle(Redirect... redirects) {
        Collections.addAll(this.redirects, redirects);
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(Environment environment) {
        environment.servlets().addFilter(this.getClass().getName(), new Filter() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
                    throws IOException, ServletException {
                if (req instanceof HttpServletRequest) {
                    HttpServletRequest request = (HttpServletRequest) req;

                    for (Redirect redirect : redirects) {
                        String redirectUrl = redirect.getRedirect(request);
                        if (redirectUrl != null) {
                            HttpServletResponse response = (HttpServletResponse) res;

                            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                            response.setHeader(HttpHeaders.LOCATION, redirectUrl);
                            return;
                        }
                    }
                }

                chain.doFilter(req, res);
            }

            @Override
            public void destroy() { /* unused */ }

            @Override
            public void init(FilterConfig filterConfig) throws ServletException { /* unused */ }
        }).addMappingForUrlPatterns(null, false, "*");
    }
}
