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

package com.boyuanitsm.fort.sdk.context;

/**
 * Given {@link FortContext} with the current execution thread.
 *
 * @author zhanghua on 5/17/16.
 */
public class FortContextHolder {

    private static FortContext context;

    public static FortContext getContext() {
        return context;
    }

    public static void setContext(FortContext context) {
        FortContextHolder.context = context;
    }
}
