package archiver;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

public class HtmlPage {
    private Document htmlContents;

    public HtmlPage(URL url) throws IOException {
    	Connection connection = Jsoup.connect(url.toString());
    	connection.timeout(10000);
        this.htmlContents = connection.get();
    }

    public Elements selectElements(String selector) throws IOException {
        Elements elements = htmlContents.select(selector);
        if (elements.isEmpty()) {
            throw new IOException("Elements list was empty");
        }
        return elements;
    }
}
