/*
 * Copyright 2016-2017 Shanghai Boyuan IT Services Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
