package zerobase.divitrack.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zerobase.divitrack.model.Company;
import zerobase.divitrack.model.ScrapedResult;
import zerobase.divitrack.model.constants.CacheKey;
import zerobase.divitrack.persist.CompanyRepository;
import zerobase.divitrack.persist.DividendRepository;
import zerobase.divitrack.persist.entity.CompanyEntity;
import zerobase.divitrack.persist.entity.DividendEntity;
import zerobase.divitrack.scraper.Scrapper;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@EnableCaching
@Component
public class ScrapperScheduler {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final Scrapper yahooFinanceScrapper;

    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduler() {
        log.info("Scrapping scheduler is started");
        //저장된 회사 목록을 조회
        List<CompanyEntity> companies = this.companyRepository.findAll();
        //회사마다 배당금 정보 새로 스크래핑
        for (var company : companies) {
            log.info("Scrapping scheduler is stared, CompanyName: " + company.getName());
            ScrapedResult scrapedResult = yahooFinanceScrapper.scrap(new Company(company.getTicker(), company.getName()));

            //스크래핑 한 배당금 정보 중 디비에 없는 값은 저장
            scrapedResult.getDividends().stream()
                    .map(e -> new DividendEntity(company.getId(), e))
                    .forEach(e -> {
                        Boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if (!exists) {
                            this.dividendRepository.save(e);
                        }

                    });

            //연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지. 서버에 부하주면 안됌.
            try {
                Thread.sleep(3000); // 3 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }

    }


}
