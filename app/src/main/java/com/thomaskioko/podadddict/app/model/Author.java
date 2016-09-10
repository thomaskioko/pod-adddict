package com.thomaskioko.podadddict.app.model;

/**
 * @author Thomas Kioko
 */

public class Author {

    private Name name;
    private Uri uri;

    /**
     * @return The name
     */
    public Name getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(Name name) {
        this.name = name;
    }

    /**
     * @return The uri
     */
    public Uri getUri() {
        return uri;
    }

    /**
     * @param uri The uri
     */
    public void setUri(Uri uri) {
        this.uri = uri;
    }

}
