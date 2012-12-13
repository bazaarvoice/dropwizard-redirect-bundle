package com.bazaarvoice.dropwizard.redirect;

import com.google.common.collect.ImmutableMap;
import com.yammer.dropwizard.Bundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class RedirectBundle implements Bundle {
    private final Map<String, String> uriMapping;

    public RedirectBundle(String requestUri, String redirectUri) {
        this.uriMapping = ImmutableMap.of(requestUri, redirectUri);
    }

    public RedirectBundle(Map<String, String> uriMapping) {
        this.uriMapping = ImmutableMap.copyOf(uriMapping);
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(Environment environment) {
        environment.addFilter(new Filter() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
                    throws IOException, ServletException {
                if (req instanceof HttpServletRequest) {
                    HttpServletRequest request = (HttpServletRequest) req;

                    String uri = request.getRequestURI();
                    String redirectUri = uriMapping.get(uri);
                    if (redirectUri != null) {
                        HttpServletResponse response = (HttpServletResponse) res;
                        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                        response.setHeader("Location", redirectUri);
                        response.setHeader("Connection", "close");
                        return;
                    }
                }

                chain.doFilter(req, res);
            }

            @Override
            public void destroy() { /* unused */ }

            @Override
            public void init(FilterConfig filterConfig) throws ServletException { /* unused */ }
        }, "*");
    }
}
