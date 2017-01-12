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

import com.boyuanitsm.fort.sdk.domain.TreeSecurityNav;

import java.util.Comparator;

/**
 * TreeSecurityNav Comparator
 * <p>
 * If first nav.position > second nav.position return 1;
 * If first nav.position = second nav.position return 1;
 * If first nav.position < second nav.position return -1;
 *
 * @author hookszhang on 09/12/2016.
 */
public class TreeSecurityNavComparator implements Comparator<TreeSecurityNav> {
    @Override
    public int compare(TreeSecurityNav o1, TreeSecurityNav o2) {
        double first = o1.getNav().getPosition();
        double second = o2.getNav().getPosition();
        return first >= second ? 1 : -1;
    }
}
