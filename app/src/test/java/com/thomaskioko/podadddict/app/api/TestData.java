package com.thomaskioko.podadddict.app.api;

/**
 * @author Thomas Kioko
 */
public interface TestData {

    /**
     * This ID is required by iTunes when making a request. For more info
     * {@see <href="https://affiliate.itunes.apple.com/resources/documentation/itunes-store-web-service-search-api/#lookup">}
     */
    String LOOK_UP_ID = "523121474";
    /**
     * Test Feed URL. This is returned after invoking iTunes Lookup endpoint
     */
    String FEED_URL = "http://feeds.feedburner.com/RevisionistHistory";
}
