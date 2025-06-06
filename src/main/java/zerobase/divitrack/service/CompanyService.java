package zerobase.divitrack.service;

import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import zerobase.divitrack.model.Company;
import zerobase.divitrack.model.ScrapedResult;
import zerobase.divitrack.persist.CompanyRepository;
import zerobase.divitrack.persist.DividendRepository;
import zerobase.divitrack.persist.entity.CompanyEntity;
import zerobase.divitrack.persist.entity.DividendEntity;
import zerobase.divitrack.scraper.Scrapper;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CompanyService {

  private final Trie trie;
  private final Scrapper yahooFinanceScrapper;
  private final CompanyRepository companyRepository;
  private final DividendRepository dividendRepository;

  public Company save(String ticker) {
    boolean exists = companyRepository.existsByTicker(ticker);
    if (exists) {
      throw new RuntimeException("already exists ticker -> " + ticker);
    }
    return this.storeCompanyAndDividend(ticker);
  }

  private Company storeCompanyAndDividend(String ticker) {
    // ticker 를 기준으로 회사를 스크래핑
    Company company = this.yahooFinanceScrapper.scrapCompanyByTicker(ticker);
    if (ObjectUtils.isEmpty(company)) {
      throw new RuntimeException("Failed to scrap ticker -> " + ticker);
    }
    // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
    ScrapedResult scrapedResult = this.yahooFinanceScrapper.scrap(company);
    // 스크래핑 결과
    CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
    List<DividendEntity> dividendEntityList = scrapedResult.getDividends().stream()
        .map(e -> new DividendEntity(companyEntity.getId(), e))
        .collect(Collectors.toList());

    this.dividendRepository.saveAll(dividendEntityList);
    return company;
  }

  public Page<CompanyEntity> getAllCompany(Pageable pageable) {
    return this.companyRepository.findAll(pageable);
  }

  public void addAutocompleteKeyword(String keyword) {
    this.trie.put(keyword, null);
  }

  //Like 연산자를 활용하여 자동 완성 기능 구현 해봄
  public List<String> getCompanyNamesByKeyword(String keyword) {
    Pageable limit = PageRequest.of(0, 10);
    Page<CompanyEntity> companyEntities = this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
    return companyEntities.stream()
        .map(e -> e.getName())
        .collect(Collectors.toList());

  }

  public List<String> autocomplete(String keyword) {
    return (List<String>) this.trie.prefixMap(keyword).keySet()
        .stream().collect(Collectors.toList());
  }

  public void deleteAutocompleteKeyword(String keyword) {
    this.trie.remove(keyword);
  }

  public String deleteCompany(String ticker) {
    var company = this.companyRepository.findByTicker(ticker)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 회사입니다."));

    this.dividendRepository.deleteAllByCompanyId(company.getId());
    this.companyRepository.delete(company);

    this.deleteAutocompleteKeyword(company.getName());

    return company.getName();
  }
}
