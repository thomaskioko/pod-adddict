package com.thomaskioko.podadddict.app.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Thomas Kioko
 */

public class Enclosure {
    @SerializedName(value = "@url")
    private String url;
    @SerializedName(value = "@length")
    private String length;
    @SerializedName(value = "@type")
    private String type;

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The @url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return The length
     */
    public String getLength() {
        return length;
    }

    /**
     * @param length The @length
     */
    public void setLength(String length) {
        this.length = length;
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
}
