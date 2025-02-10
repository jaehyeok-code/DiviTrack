package zerobase.divitrack.web;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import zerobase.divitrack.model.Company;
import zerobase.divitrack.persist.CompanyRepository;
import zerobase.divitrack.persist.entity.CompanyEntity;
import zerobase.divitrack.service.CompanyService;

import java.util.List;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyRepository companyRepository;

    //회사명검색시 자동완성 API
    @GetMapping("/autocomplete")
    public ResponseEntity<?> autoComplete(@RequestParam String keyword) {
        // trie 를 이용한 자동 완성 기능
        // List<String> result = this.companyService.autocomplete(keyword);
        // 쿼리의 like 연산자를 이용한 자동 완성 기능
        List<String> likeResult = this.companyService.getCompanyNamesByKeyword(keyword);
        return ResponseEntity.ok(likeResult);
    }
    // 회사 리스트 조회 API, Pagable 은 쿼리 파라미터로 page size 등을 받을 수 있음, Spring Data Jpa 가 자동으로 처리 해줌.
    @GetMapping
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> searchCompany(Pageable pageable) {
        Page<CompanyEntity> companies = this.companyService.getAllCompany(pageable);
        return  ResponseEntity.ok(companies);
    }

    //배당금 데이터 저장 API
    @PostMapping
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        String ticker = request.getTicker().trim();
        if(ObjectUtils.isEmpty(ticker)) {
            throw new RuntimeException("Ticker is empty");
        }
        Company company = this.companyService.save(ticker);
        this.companyService.addAutocompleteKeyword(company.getName());
        return ResponseEntity.ok(company);
    }

    //배당금 데이터 삭제 API
    @DeleteMapping
    public ResponseEntity<?> deleteCompany() {
        return null;
    }
}
