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

package com.boyuanitsm.fort.sdk.domain;

import com.boyuanitsm.fort.sdk.util.TreeSecurityNavComparator;

import java.util.*;

/**
 * The tree security nav.
 *
 * @author zhanghua on 5/19/16.
 */
public class TreeSecurityNav {

    private static final TreeSecurityNavComparator treeSecurityNavComparator = new TreeSecurityNavComparator();

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

        for (SecurityNav nav : navs) {
            TreeSecurityNav child = transform(new TreeSecurityNav(nav));
            root.addChild(child);
        }

        sort(root.children);
        return root.children;
    }

    /**
     * Sort TreeSecurityNav
     * <p>
     * If first nav.position > second nav.position return 1;
     * If first nav.position = second nav.position return 1;
     * If first nav.position < second nav.position return -1;
     *
     * @param treeSecurityNavs
     */
    private static void sort(List<TreeSecurityNav> treeSecurityNavs) {
        Collections.sort(treeSecurityNavs, treeSecurityNavComparator);

        // recursion sort children
        for (TreeSecurityNav tree : treeSecurityNavs) {
            if (tree.getChildren().size() > 0) {
                sort(tree.getChildren());
            }
        }
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
