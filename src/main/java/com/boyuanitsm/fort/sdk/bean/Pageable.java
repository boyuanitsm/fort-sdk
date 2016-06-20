package com.boyuanitsm.fort.sdk.bean;

/**
 * The pagination information
 *
 * @author hookszhang on 6/20/16.
 */
public class Pageable {

    public Pageable() {
        this.page = 0;
        this.size = Integer.MAX_VALUE;
    }

    public Pageable(int page, int size) {
        this.page = page;
        this.size = size;
    }

    private int page;

    private int size;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
