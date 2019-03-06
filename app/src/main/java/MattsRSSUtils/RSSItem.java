package MattsRSSUtils;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class RSSItem implements Serializable {
    private String title = "",
            description = "",
            author = "",
            link = "";
    private Date pubDate;

    public RSSItem(final String title,
                   final String link,
                   final String description,
                   final String author,
                   final Date pubDate) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (author != null) this.author = author;
        this.link = link;
        this.pubDate = pubDate;
    }

    public RSSItem() {
        this.link = null;
        this.pubDate = null;
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
        if (title.equals(""))
            this.title = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }
}