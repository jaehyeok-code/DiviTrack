package zerobase.divitrack.service;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import zerobase.divitrack.model.Company;
import zerobase.divitrack.model.Dividend;
import zerobase.divitrack.model.ScrapedResult;
import zerobase.divitrack.model.constants.CacheKey;
import zerobase.divitrack.persist.CompanyRepository;
import zerobase.divitrack.persist.DividendRepository;
import zerobase.divitrack.persist.entity.CompanyEntity;
import zerobase.divitrack.persist.entity.DividendEntity;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {

        // 1. 회사명을 기준으로 회사 정보를 조회, orElseThrow() 는 예외터트리거나 있으면 Optional<> 벗겨서 반환
        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명 입니다."));

        // 2. 조회된 회사 ID로 배당금 정보 조회
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());

        // 3. 결과 조합 후 반환
        List<Dividend> dividends = dividendEntities.stream()
                .map(e -> new Dividend(e.getDate(), e.getDividend()))
                .collect(Collectors.toList());

        return new ScrapedResult(new Company(company.getTicker(), company.getName()), dividends);

    }
}
