package com.boyuanitsm.fort.sdk.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The tree security nav.
 *
 * @author zhanghua on 5/19/16.
 */
public class TreeSecurityNav {

    private TreeSecurityNav() {
        this.treeId = 0L;
    }

    private TreeSecurityNav(SecurityNav nav) {
        this.treeId = nav.getId();
        this.nav = nav;
    }

    /**
     * Build tree security nav
     *
     * @param navs the collection of the SecurityNav
     * @return the navs of the tree
     */
    public static List<TreeSecurityNav> build(Collection<SecurityNav> navs) {
        // init tree root
        TreeSecurityNav root = new TreeSecurityNav();

        for (SecurityNav nav: navs) {
            TreeSecurityNav child = transform(new TreeSecurityNav(nav));
            root.addChild(child);
        }

        return root.children;
    }

    /**
     * Add child to this.children.
     *
     * @param child the child.
     */
    private void addChild(TreeSecurityNav child) {
        // find child
        TreeSecurityNav parent = this.findChildByTreeId(child.treeId);
        if (parent == null) {
            // if not found, direct add child to root.
            this.children.add(child);
        } else {
            // if founded, add child to parent.
            if (child.children.size() > 0) {
                parent.addChild(child.children.get(0));
            }
        }
    }

    /**
     * transform. from child-parent-parent-parent to parent-child-child-child.
     * each TreeSecurityNav only has one child.
     *
     * @param child child
     * @return the result of the transform
     */
    private static TreeSecurityNav transform(TreeSecurityNav child) {
        // get child parent
        SecurityNav parent = child.getNav().getParent();

        if (parent != null) {
            // new parent
            TreeSecurityNav parent2 = new TreeSecurityNav(parent);
            // add to new parent
            parent2.children.add(child);
            // recursion
            return transform(parent2);
        }

        return child;
    }


    /**
     * Find child by tree id
     *
     * @param treeId the treeId of the TreeSecurityNav
     * @return if not found return null else child
     */
    private TreeSecurityNav findChildByTreeId(long treeId) {
        if (this.treeId == treeId) {
            return this;
        }

        // foreach and find
        for (TreeSecurityNav child : this.children) {
            if (child.treeId == treeId) {
                // founded return child
                return child;
            }
        }

        // not found
        return null;
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
