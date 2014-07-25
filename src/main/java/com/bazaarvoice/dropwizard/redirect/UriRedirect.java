package com.bazaarvoice.dropwizard.redirect;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/** Regular expression based redirect.  Has access to the full URI. */
public class UriRedirect implements Redirect {
    private final List<Entry> entries;

    public UriRedirect(String regex, String replacement) {
        checkNotNull(regex);
        checkNotNull(replacement);

        entries = ImmutableList.of(new Entry(Pattern.compile(regex), replacement));
    }

    public UriRedirect(Map<String, String> uriMap) {
        checkNotNull(uriMap);

        entries = Lists.newArrayList();
        for (Map.Entry<String, String> entry : uriMap.entrySet()) {
            String regex = entry.getKey();
            String replacement = entry.getValue();
            entries.add(new Entry(Pattern.compile(regex), replacement));
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getRedirect(HttpServletRequest request) {
        String uri = getFullURI(request);
        for (Entry entry : entries) {
            Matcher matcher = entry.getRegex().matcher(uri);
            if (matcher.matches()) {
                return matcher.replaceAll(entry.getReplacement());
            }
        }

        return null;
    }

    private static String getFullURI(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    private static final class Entry {
        Pattern regex;
        String replacement;

        Entry(Pattern regex, String replacement) {
            this.regex = regex;
            this.replacement = replacement;
        }

        Pattern getRegex() {
            return regex;
        }

        String getReplacement() {
            return replacement;
        }
    }
}