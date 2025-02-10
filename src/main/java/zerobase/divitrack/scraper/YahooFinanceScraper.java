package zerobase.divitrack.scraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import zerobase.divitrack.model.Company;
import zerobase.divitrack.model.Dividend;
import zerobase.divitrack.model.ScrapedResult;
import zerobase.divitrack.model.constants.Month;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scrapper {

    //heap영역에 멤버변수로 설정해줌으로서 메서드 내에 포함시키는것보다 메모리 절약이 가능
    //하지만 모든 변수를 멤버변수로 빼게 되면 힙영역이 줄고 동작시키는데있어 힙영역이 찰때마다 가비지 컬렉션이 수행됨에 따라 성능이 저하된다. 따라서 필요한것만!
    private static final String STATISTIC_URL = "https://finance.yahoo.com/quote/%s/history/?period1=%d&period2=%d&interval=1mo";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_TIME = 86400;

    @Override
    public ScrapedResult scrap(Company company) {
        //var은 변수타입추론 기능, 지역변수에서 가능
        var scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);

        try {
            long now = System.currentTimeMillis() / 1000;
            String url = String.format(STATISTIC_URL, company.getTicker(), START_TIME, now);
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            Elements parsingDivs = document.select("table");
            Element table = parsingDivs.getFirst();
            Element tbody = table.select("tbody").first();

            List<Dividend> dividends = new ArrayList<>();
            for (Element e : tbody.children()) {
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {
                    continue;
                }

                String[] split = txt.split(" ");
                int month = Month.strToNumber(split[0]);
                int day = Integer.parseInt(split[1].replace(",", ""));
                int year = Integer.parseInt(split[2]);
                String dividend = split[3];

                if (month < 0) {
                    throw new RuntimeException("unexpected month enum value -> " + split[0]);
                }

                dividends.add(new Dividend(LocalDateTime.of(year, month, day, 0, 0),dividend));

            }

            scrapResult.setDividends(dividends);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return scrapResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {

        try {
            String url = String.format(SUMMARY_URL, ticker, ticker);
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();
            Element titleEle = document.getElementsByClass("yf-xxbei9").first();

            // 정규표현식에 대해 좀더 깊게 학습할것.
            String title = titleEle.text().replaceAll("\\s*\\(.*?\\)", "");

            return new Company(ticker, title);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
