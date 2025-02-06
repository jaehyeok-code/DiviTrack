package zerobase.divitrack.web;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import zerobase.divitrack.model.Company;
import zerobase.divitrack.service.CompanyService;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    //회사명검색시 자동완성 API
    @GetMapping("/autocomplete")
    public ResponseEntity<?> autoComplete(@RequestParam String keyword) {
        return null;
    }
    // 회사리스트조회 API
    @GetMapping
    public ResponseEntity<?> searchCompany() {
        return null;
    }

    //배당금 데이터 저장 API
    @PostMapping
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        String ticker = request.getTicker().trim();
        if(ObjectUtils.isEmpty(ticker)) {
            throw new RuntimeException("Ticker is empty");
        }
        Company company = this.companyService.save(ticker);
        return ResponseEntity.ok(company);
    }

    //배당금 데이터 삭제 API
    @DeleteMapping
    public ResponseEntity<?> deleteCompany() {
        return null;
    }
}
