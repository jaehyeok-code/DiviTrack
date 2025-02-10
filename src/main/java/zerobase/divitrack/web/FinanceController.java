package zerobase.divitrack.web;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.divitrack.model.ScrapedResult;
import zerobase.divitrack.service.FinanceService;

@RestController
@RequestMapping("/finance")
@AllArgsConstructor
public class FinanceController {
    private final FinanceService financeService;

    //해당 회사의 배당금 조회 API
    @GetMapping("/dividend/{companyName}")
    public ResponseEntity<?> searchFinance(@PathVariable("companyName") String companyName) {
        ScrapedResult result = financeService.getDividendByCompanyName(companyName);
        return ResponseEntity.ok(result);
    }
}
