package com.thomaskioko.podadddict.app.model;

/**
 * @author Thomas Kioko
 */

public class Enclosure {
    private String url;
    private String length;
    private String type;

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The @url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     *
     * @return
     * The length
     */
    public String getLength() {
        return length;
    }

    /**
     *
     * @param length
     * The @length
     */
    public void setLength(String length) {
        this.length = length;
    }

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The @type
     */
    public void setType(String type) {
        this.type = type;
    }
}
