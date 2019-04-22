package me.goddragon.teaseai.utils.libraries.ripme.ripper.rippers;

import me.goddragon.teaseai.utils.libraries.ripme.ripper.AbstractHTMLRipper;
import me.goddragon.teaseai.utils.libraries.ripme.utils.Http;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JagodibujaRipper extends AbstractHTMLRipper {

    public JagodibujaRipper(URL url) throws IOException {
        super(url);
    }

    @Override
    public String getHost() {
        return "jagodibuja";
    }

    @Override
    public String getDomain() {
        return "jagodibuja.com";
    }

    @Override
    public String getGID(URL url) throws MalformedURLException {
        Pattern p = Pattern.compile("^https?://www.jagodibuja.com/([a-zA-Z0-9_-]*)/?");
        Matcher m = p.matcher(url.toExternalForm());
        if (m.matches()) {
            return m.group(1);
        }
        throw new MalformedURLException("Expected jagodibuja.com gallery formats hwww.jagodibuja.com/Comic name/ got " + url + " instead");
    }

    @Override
    public Document getFirstPage() throws IOException {
        // "url" is an instance field of the superclass
        return Http.url(url).get();
    }

    @Override
    public List<String> getURLsFromPage(Document doc) {
        List<String> result = new ArrayList<>();
        for (Element comicPageUrl : doc.select("div.gallery-icon > a")) {
            // Check if the ripper has been stopped
            try {
                stopCheck();
            } catch (IOException e) {
                return result;
            }
            try {
                sleep(500);
                Document comicPage = Http.url(comicPageUrl.attr("href")).get();
                Element elem = comicPage.select("span.full-size-link > a").first();
                LOGGER.log(Level.INFO, "Got link " + elem.attr("href"));
                try {
                    addURLToDownload(new URL(elem.attr("href")), "");
                } catch (MalformedURLException e) {
                    LOGGER.log(Level.WARNING, "Malformed URL");
                    e.printStackTrace();
                }
                result.add(elem.attr("href"));
            } catch (IOException e) {
                LOGGER.log(Level.INFO, "Error loading " + comicPageUrl);
            }
        }
        return result;
    }

    @Override
    public void downloadURL(URL url, int index) {
        // sleep(500);
        // addURLToDownload(url, getPrefix(index));
    }

}
