package com.pj.portfoliosite.portfoliosite.portfolio.bookmark;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PortfolioBookMarkController {
    private final PortfolioBookMarkService portfolioBookMarkService;
    @PostMapping("/portfolio/{id}/bookmark")
    public ResponseEntity<DataResponse> portfolioLike(
            @PathVariable Long id
    ){
        DataResponse dataResponse = new DataResponse();
        dataResponse.setData(null);

        dataResponse.setMessage("success");
        portfolioBookMarkService.portfolioBookMark(id);
        return ResponseEntity.ok(dataResponse);
    }
    @DeleteMapping("/portfolio/{id}/bookmark")
    public ResponseEntity<DataResponse> deleteLike(
            @PathVariable Long id
    ){
        DataResponse dataResponse = new DataResponse();
        dataResponse.setData(null);

        dataResponse.setMessage("success");
        portfolioBookMarkService.portfolioBookMarkDelete(id);
        return ResponseEntity.ok(dataResponse);
    }
}
