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
