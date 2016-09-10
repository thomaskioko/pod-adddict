package com.thomaskioko.podadddict.app.model;

/**
 * @author Thomas Kioko
 */

public class Rss {
    private String version;
    private Channel channel;

    /**
     *
     * @return
     * The version
     */
    public String getVersion() {
        return version;
    }

    /**
     *
     * @param version
     * The @version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     *
     * @return
     * The channel
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     *
     * @param channel
     * The channel
     */
    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
