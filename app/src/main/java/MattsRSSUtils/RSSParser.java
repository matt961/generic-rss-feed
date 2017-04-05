package MattsRSSUtils;

/**
 * Created by Matt on 3/26/2017.
 */

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class RSSParser {
    public static final int CH_TITLE = 0;
    public static final int CH_URL = 1;
    public static final int CH_DESC = 2;
    public static final int CH_LASTBD = 3;
    public static final int CH_CAT = 4;
    public static final int CH_IMGURL = 5;
    private static final DateFormat rfc822Date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z",
            Locale.CANADA);


    // actual posts to the RSS feed delimited by <item>
    private ArrayList<RSSItem> feedItems;
    // used for metadata of the RSS feed. Everything in <channelMetaData> except <item> tags.
    private RSSChannel channelMetaData;
    private Document rssFeedDocument;

    /**
     * Take in a {@link Document} as a local field.
     *
     * @param rssFeedDocument A document parsed by {@link RSSFetcher}. Use getRssDocument() from {@link RSSFetcher}.
     */
    public RSSParser(Document rssFeedDocument) {
        this.rssFeedDocument = rssFeedDocument;
        this.channelMetaData = new RSSChannel();
        this.feedItems = new ArrayList<>();
    }

    public RSSParser() {
    }

    /**
     * Parses top level meta data for the rss feed's channelMetaData tag.
     *
     * @return <b>true</b> successfully parsed. <b>false</b> not able to be parsed.
     */
    public boolean parseDocument() {
        if (!docIsCreated()) {
            return false;
        }
        NodeList feedNodes = rssFeedDocument.getDocumentElement().getChildNodes();
        for (int feednode = 0; feednode < feedNodes.getLength(); feednode++) {
            if (feedNodes.item(feednode).getNodeName().equals("channel")) {
                NodeList channelNodes = feedNodes.item(feednode).getChildNodes();
                // channelNodes has metadata nodes as well as <item> nodes that need to be filtered.
                for (int chnode = 0; chnode < channelNodes.getLength(); chnode++) {
                    switch (channelNodes.item(chnode).getNodeName()) {
                        // required channelMetaData fields
                        case "title":
                            channelMetaData.setTitle(channelNodes.item(chnode).getTextContent());
                            break;
                        case "link":
                            channelMetaData.setLink(channelNodes.item(chnode).getTextContent());
                            break;
                        case "description":
                            channelMetaData.setDescription(channelNodes.item(chnode).getTextContent());
                            break;
                        // optional channelMetaData fields
                        case "lastBuildDate":
                            try {
                                channelMetaData.setLastBuildDate(rfc822Date.parse(channelNodes.item(
                                        chnode).getTextContent()));
                            } catch (ParseException pe) {
                                pe.printStackTrace();
                                channelMetaData.setLastBuildDate(null);
                            }
                            break;
                        case "category":
                            channelMetaData.setCategory(channelNodes.item(chnode).getTextContent());
                            break;
                        // only need the URL from this Node
                        case "image":
                            NodeList imgnodes = channelNodes.item(chnode).getChildNodes();
                            for (int urlnode = 0; urlnode < imgnodes.getLength(); urlnode++) {
                                if (imgnodes.item(urlnode).getNodeName().equals("url")) {
                                    // TODO NOTE- probably wont be part of the Android app. We'll see
                                    channelMetaData.setChannelImageURL(imgnodes.item(urlnode).getTextContent());
                                }
                            }
                            break;
                        case "item":
                            RSSItem item = new RSSItem();
                            NodeList itemCNodes = channelNodes.item(chnode).getChildNodes();
                            for (int icnode = 0; icnode < itemCNodes.getLength(); icnode++) {
                                Node n = itemCNodes.item(icnode);
                                switch (n.getNodeName()) {
                                    case "title":
                                        item.setTitle(n.getTextContent());
                                        break;
                                    case "link":
                                        item.setLink(n.getTextContent());
                                        break;
                                    case "description":
                                        item.setDescription(n.getTextContent());
                                        break;
                                    case "author":
                                        item.setAuthor(n.getTextContent());
                                        break;
                                    case "pubDate":
                                        try {
                                            item.setPubDate(rfc822Date.parse(n.getTextContent()));
                                        } catch (ParseException pe) {
                                            pe.printStackTrace();
                                            item.setPubDate(null);
                                        }
                                        break;
                                }
                            }
                            feedItems.add(item);
                    }
                }
            }
        }
        return true;
    }

    public ArrayList<RSSItem> getFeedItems() {
        return feedItems;
    }

    public RSSChannel getChannelMetaData() {
        return channelMetaData;
    }

    /**
     * {@link RSSFetcher} grabs a feed off the internet for this class to handle.
     *
     * @return <b>true</b> if the doc is not null. <b>false</b> otherwise
     */
    public boolean docIsCreated() {
        return rssFeedDocument != null;
    }
}