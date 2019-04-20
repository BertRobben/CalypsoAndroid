package bert.calypso.crawler;

import android.os.Parcelable;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import bert.calypso.SimpleTime;


public class GamesExtractor {

    private final ProgressPublisher publisher;

    public GamesExtractor(ProgressPublisher publisher) {
        this.publisher = publisher;
    }

    public List<Game> crawl() throws IOException {

        publisher.publish("Connecting to vlmbrabant.be");
        Response response = Jsoup.connect("https://vlmbrabant.be/linkscomp.htm").method(Connection.Method.GET)
                .execute();
        Document doc = response.parse();

        String regioOostUrl = HtmlHelper.findLinkWithImage(doc, "knoppen/regio oost.jpg");
        publisher.publish("Connecting to " + regioOostUrl);

        response = Jsoup.connect(regioOostUrl).method(Connection.Method.GET)
                .referrer(response.url().toString())
                .execute();

        doc = response.parse();

        return extractGames(doc);
    }

    private List<Game> extractGames(Document doc) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        List<Game> result = new ArrayList<>();
        for (Element tr : doc.select("tr")) {
            Elements trs = tr.getElementsByTag("td");
            try {
                Date date = trs.get(2).text().length() > 0 ? sdf.parse(trs.get(2).text()) : null;
                String home = trs.get(4).text();
                String out = trs.get(5).text();
                String startTime = trs.get(3).text();
                int index = startTime.indexOf(':');
                SimpleTime start = index > 0 ? new SimpleTime(Integer.parseInt(startTime.substring(0, index)), Integer.parseInt(startTime.substring(index + 1))) : null;
                boolean calypsoGame = home.contains("CALYPSO") || out.contains("CALYPSO");
                if (!calypsoGame) {
                    continue;
                }
                if ("A".equals(out) || "A".equals(home)) {
                    continue;
                }
                boolean homeGame = home.contains("CALYPSO");
                result.add(new Game(date, homeGame ? out : home, start, homeGame));
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
        publisher.publish("Extracted games");
        return result;
    }

}
