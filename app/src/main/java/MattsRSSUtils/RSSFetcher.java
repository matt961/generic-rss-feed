package MattsRSSUtils;
/**
 * Created by Matt on 3/26/2017.
 */

import android.util.Log;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class RSSFetcher {
    private Document rssDocument;
    private URL rssFeedURL;

    /**
     * Default constructor.
     *
     * @param rssFeedURL The URL of the RSS feed to fetch.
     * @throws MalformedURLException If a junk URL was passed.
     */
    public RSSFetcher(URL rssFeedURL) throws MalformedURLException {
        if (rssFeedURL == null) {
            throw new MalformedURLException("Cannot assign rssFeedURL to null URL.");
        }
        this.rssFeedURL = rssFeedURL;
        this.rssDocument = null;
    }

    /**
     * Downloads an RSS feed and creates a DOM object of type {@link Document} that is assigned to field rssDocument.
     * See parseXML method.
     *
     * @return 0 if successfully downloaded and parsed into a {@link Document} or 1 if there was an error.
     */
    public int fetchDocument() {
        try (InputStream rssInputStream = rssFeedURL.openConnection().getInputStream()) {
            Log.v("fetchDOcument", rssFeedURL.toString());
            rssDocument = parseXML(rssInputStream);
        } catch (IOException | ParserConfigurationException | SAXException ioe) {
            ioe.printStackTrace();
            return 1;
        }
        return 0;
    }

    /**
     * @param stream The InputStream from the URL connection to parse into a {@link Document}
     * @return org.w3c.dom.Document that represents the RSS feed.
     * @throws IOException                  Reading from the connection
     * @throws SAXException                 Parsing error
     * @throws ParserConfigurationException Parsing Error
     */
    private Document parseXML(InputStream stream) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();
        return objDocumentBuilder.parse(stream);
    }

    public Document getRssDocument() {
        return rssDocument;
    }

    public void setRssDocument(Document rssDocument) {
        this.rssDocument = rssDocument;
    }

    public URL getRssFeedURL() {
        return rssFeedURL;
    }

    public void setRssFeedURL(URL rssFeedURL) {
        this.rssFeedURL = rssFeedURL;
    }
}