package com.thomaskioko.podadddict.app.api.model;

/**
 * @author Thomas Kioko
 */

public class AtomLink {
    private String rel;
    private String type;
    private String href;

    /**
     * @return The rel
     */
    public String getRel() {
        return rel;
    }

    /**
     * @param rel The @rel
     */
    public void setRel(String rel) {
        this.rel = rel;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The @type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The href
     */
    public String getHref() {
        return href;
    }

    /**
     * @param href The @href
     */
    public void setHref(String href) {
        this.href = href;
    }
}
