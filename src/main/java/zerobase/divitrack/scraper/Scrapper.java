package zerobase.divitrack.scraper;

import zerobase.divitrack.model.Company;
import zerobase.divitrack.model.ScrapedResult;

public interface Scrapper {

    public ScrapedResult scrap(Company company);
    public Company scrapCompanyByTicker(String ticker);


}
