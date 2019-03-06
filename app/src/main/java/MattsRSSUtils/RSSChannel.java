package MattsRSSUtils;

import java.io.Serializable;
import java.util.Date;

public class RSSChannel implements Serializable {
    private String title,
            description,
            category,
            link,
            channelImageURL,
            linkOfRSS;
    private Date lastBuildDate;

    /**
     * Default constructor
     */
    public RSSChannel() {
        title = "";
        description = "";
        category = "";
        link = "";
        channelImageURL = "";
        lastBuildDate = null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getChannelImageURL() {
        return channelImageURL;
    }

    public void setChannelImageURL(String channelImageURL) {
        this.channelImageURL = channelImageURL;
    }

    public Date getLastBuildDate() {
        return lastBuildDate;
    }

    public void setLastBuildDate(Date lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }

    public String getLinkOfRSS() {
        return linkOfRSS;
    }

    public void setLinkOfRSS(String linkOfRSS) {
        this.linkOfRSS = linkOfRSS;
    }
}
