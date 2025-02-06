package zerobase.divitrack;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import zerobase.divitrack.model.Company;
import zerobase.divitrack.scraper.YahooFinanceScraper;

import java.io.IOException;

@SpringBootApplication
public class DiviTrackApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiviTrackApplication.class, args);

    }

}
