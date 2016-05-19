package com.boyuanitsm.fort.sdk.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The tree security nav.
 *
 * @author zhanghua on 5/19/16.
 */
public class TreeSecurityNav {

    private TreeSecurityNav() {
    }

    /**
     * Build Tree security nav
     *
     * @param navSet the set of the SecurityNav
     * @return the tree security nav
     */
    public static TreeSecurityNav build(Set<SecurityNav> navSet) {
        // TODO implement
        return new TreeSecurityNav();
    }

    private Long treeId;

    private SecurityNav nav;

    private List<TreeSecurityNav> children = new ArrayList<TreeSecurityNav>();

    public Long getTreeId() {
        return treeId;
    }

    public void setTreeId(Long treeId) {
        this.treeId = treeId;
    }

    public SecurityNav getNav() {
        return nav;
    }

    public void setNav(SecurityNav nav) {
        this.nav = nav;
    }

    public List<TreeSecurityNav> getChildren() {
        return children;
    }

    public void setChildren(List<TreeSecurityNav> children) {
        this.children = children;
    }
}
