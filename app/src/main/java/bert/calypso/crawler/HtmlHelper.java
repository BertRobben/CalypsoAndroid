package bert.calypso.crawler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HtmlHelper {
    public static String findLink(Document document, String text) {
        Elements linksOnPage = document.select("a[href]");
        for (Element link : linksOnPage) {
            if (text.equals(link.text())) {
                return link.attr("abs:href");
            }
        }
        throw new IllegalArgumentException("No link found");
    }

    public static String findLinkWithImage(Document document, String image) {
        Elements linksOnPage = document.select("a[href]");

        //5. For each extracted URL... go back to Step 4.
        for (Element link : linksOnPage) {
            Elements imgs = link.getElementsByTag("img");
            if (!imgs.isEmpty() && image.equals(imgs.get(0).attr("src"))) {
                return link.attr("abs:href");
            }
        }

        throw new IllegalArgumentException("No link found");
    }

    public static String findOption(Document document, String select, String key) {
        Elements options = document.select("select[name=" + select + "]");

        //5. For each extracted URL... go back to Step 4.
        for (Element option : options.select("option")) {
            if (key.equals(option.text())) {
                return option.attr("value");
            }
        }

        throw new IllegalArgumentException("No option found");
    }

    /**
     * Disables the SSL certificate checking for new instances of {@link HttpsURLConnection}
     */
    public static void disableSSLCertificateChecking() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
