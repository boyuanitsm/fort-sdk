package com.boyuanitsm.fort.sdk.util;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.List;

/**
 * The url dim matcher
 *
 * @author hookszhang on 25/11/2016.
 */
public class DimMatcher {

    private PathMatcher pathMatcher = new AntPathMatcher();

    public String match(List<String> patterns, String str) {
        for (String pattern : patterns) {
            if (pathMatcher.match(pattern, str)) {
                return pattern;
            }
        }
        return null;
    }
}
