package bert.calypso.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bert.calypso.SimpleTime;


public class LocationExtractor {

    private final ProgressPublisher publisher;

    interface ReservationExtractor {
        List<Reservation> getReservations(Date date) throws IOException;
    }

    public LocationExtractor(ProgressPublisher publisher) {
        this.publisher = publisher;
        HtmlHelper.disableSSLCertificateChecking();
    }

    public ReservationExtractor createReservationExtractor() throws IOException {

        final Map<String, String> cookies = new HashMap<>();
        publisher.publish("Connecting to sport.leuven.be");
        Response response = Jsoup.connect("https://sport.leuven.be/").method(Connection.Method.GET)
                .execute();
        // this starts a new php session and gives us 2 cookies
        cookies.putAll(response.cookies());
        Document doc = response.parse();

        String clubs = HtmlHelper.findLink(doc, "Clubs");
        publisher.publish("Connecting to " + clubs);

        response = Jsoup.connect(clubs).method(Connection.Method.GET)
                .cookies(cookies)
                .referrer(response.url().toString())
                .execute();

        // javascript sets window.location to club_login.php?soort=log
        response = Jsoup.connect("https://sport.leuven.be/club_login.php?soort=log").method(Connection.Method.GET)
                .cookies(cookies)
                .referrer(response.url().toString())
                .execute();

        publisher.publish("Logging in");

        response = Jsoup.connect("https://sport.leuven.be/club_login.php?soort=log").method(Connection.Method.POST)
                .cookies(cookies)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .referrer(response.url().toString())
                .data("gn", "238") // account name Calypso
                .data("pw1", "XXXXXX")
                .data("aanloggen", "Aanloggen")
                .execute();

        publisher.publish("Getting data");
        response = Jsoup.connect("https://sport.leuven.be/clubs_mod.php").method(Connection.Method.GET)
                .cookies(cookies)
                .referrer(response.url().toString())
                .execute();

        publisher.publish("Going to calendar");
        response = Jsoup.connect("https://sport.leuven.be/index.php").method(Connection.Method.GET)
                .cookies(cookies)
                .referrer("https://sport.leuven.be/clubs_mod.php")
                .execute();

        final Document calendarDoc = response.parse();
        final String reservationsUrl = response.url().toString();

        return new ReservationExtractor() {
            @Override
            public List<Reservation> getReservations(Date date) throws IOException {
                List<Reservation> reservations = new ArrayList<>();
                reservations.addAll(LocationExtractor.this.getReservations(reservationsUrl, calendarDoc, cookies, date, "SPORTCOMPLEX KESSEL-LO"));
                reservations.addAll(LocationExtractor.this.getReservations(reservationsUrl, calendarDoc, cookies, date, "KORBEEK-LO SPORTHAL"));
                return reservations;
            }
        };
    }

//    public Reservations crawl() throws IOException {
//
//        Map<String, String> cookies = new HashMap<>();
//        publisher.publish("Connecting to sport.leuven.be");
//        Response response = Jsoup.connect("https://sport.leuven.be/").method(Connection.Method.GET)
//                .execute();
//        // this starts a new php session and gives us 2 cookies
//        cookies.putAll(response.cookies());
//        Document doc = response.parse();
//
//        String clubs = HtmlHelper.findLink(doc, "Clubs");
//        publisher.publish("Connecting to " + clubs);
//
//        response = Jsoup.connect(clubs).method(Connection.Method.GET)
//                .cookies(cookies)
//                .referrer(response.url().toString())
//                .execute();
//
//        // javascript sets window.location to club_login.php?soort=log
//        response = Jsoup.connect("https://sport.leuven.be/club_login.php?soort=log").method(Connection.Method.GET)
//                .cookies(cookies)
//                .referrer(response.url().toString())
//                .execute();
//
//        publisher.publish("Logging in");
//
//        response = Jsoup.connect("https://sport.leuven.be/club_login.php?soort=log").method(Connection.Method.POST)
//                .cookies(cookies)
//                .header("Content-Type", "application/x-www-form-urlencoded")
//                .referrer(response.url().toString())
//                .data("gn", "238") // account name Calypso
//                .data("pw1", "TZTOJP")
//                .data("aanloggen", "Aanloggen")
//                .execute();
//
//        publisher.publish("Getting data");
//        response = Jsoup.connect("https://sport.leuven.be/clubs_mod.php").method(Connection.Method.GET)
//                .cookies(cookies)
//                .referrer(response.url().toString())
//                .execute();
//
//        publisher.publish("Going to calendar");
//        response = Jsoup.connect("https://sport.leuven.be/index.php").method(Connection.Method.GET)
//                .cookies(cookies)
//                .referrer("https://sport.leuven.be/clubs_mod.php")
//                .execute();
//
//        doc = response.parse();
//
//        List<Reservation> reservations = new ArrayList<>();
//        reservations.addAll(getReservations(response.url().toString(), doc, cookies, date, "SPORTCOMPLEX KESSEL-LO"));
//        reservations.addAll(getReservations(response.url().toString(), doc, cookies, date, "KORBEEK-LO SPORTHAL"));
//        return new Reservations(reservations);
//    }

    private List<Reservation> getReservations(String url, Document doc, Map<String, String> cookies, Date date, String hall) throws IOException {
        String accomodatie = HtmlHelper.findOption(doc, "accomodaties", hall);
        publisher.publish("Getting halls for " + hall);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String dayOfMonth = Integer.toString(Calendar.DAY_OF_MONTH);
        String monthOfYear = Integer.toString(cal.get(Calendar.MONTH) + 1);
        String year = Integer.toString(cal.get(Calendar.YEAR));
        Response response = Jsoup.connect(url)
                .cookies(cookies)
                .data("accomodaties", accomodatie)
                .data("zalen", "1")
                .data("dag", dayOfMonth)
                .data("maand", monthOfYear)
                .data("jaar", year)
                .method(Connection.Method.POST)
                .execute();

        String zaal = HtmlHelper.findOption(response.parse(), "zalen", "SPORTHAL");
        publisher.publish("Getting reservation data");

        response = Jsoup.connect(response.url().toString())
                .cookies(cookies)
                .data("accomodaties", accomodatie)
                .data("zalen", zaal)
                .data("dag", dayOfMonth)
                .data("maand", monthOfYear)
                .data("jaar", year)
                .method(Connection.Method.POST)
                .execute();

        return findReservations(response.parse(), hall);
    }

    private List<Reservation> findReservations(Document document, String sportComplex) {
        List<Reservation> result = new ArrayList<>();
        Reservation current = null;
        for (Element table : document.select("table[class=box]")) {
            List<String> hours = new ArrayList<>();
            String date = null;
            Elements trs = table.select("tr");
            for (Element th : trs.get(0).select("th")) {
                if (date == null) {
                    date = th.text();
                } else {
                    hours.add(th.text());
                }
            }
            for (int j = 1; j < trs.size(); j++) {
                Elements tds = trs.get(j).select("td");
                String zaal = tds.get(0).text();
                for (int i = 1; i < tds.size(); i++) {
                    if (tds.get(i).attr("onmouseover") != null && tds.get(i).attr("onmouseover").contains("Calypso")) {
                        SimpleTime startTime = new SimpleTime(Integer.parseInt(hours.get((i - 1) / 2)), i % 2 == 0 ? 30 : 0);
                        Reservation r = new Reservation(sportComplex + "(" + zaal + ")", date, startTime, startTime.plusMinutes(30));
                        if (current == null) {
                            current = r;
                        } else if (current.canMerge(r)) {
                            current = current.merge(r);
                        } else {
                            result.add(current);
                            current = r;
                        }
                    }
                }
            }
        }
        if (current != null) {
            result.add(current);
        }
        return result;
    }

}