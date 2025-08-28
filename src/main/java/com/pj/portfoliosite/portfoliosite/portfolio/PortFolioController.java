package com.pj.portfoliosite.portfoliosite.portfolio;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.portfolio.dto.ReqPortfolioDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PortFolioController {
    private final PortFolioService portfolioService;
    @PostMapping("/portfolio")
    @Operation(summary = "포트폴리오 저장",
        description = "header에 jwt 넣어주세요"
    )
    public ResponseEntity<DataResponse> portfolioUpload(
            @RequestBody ReqPortfolioDTO reqPortfolioDTO
    ){
        portfolioService.save(reqPortfolioDTO);
        DataResponse dataResponse = new DataResponse();
        System.out.println("데이터 전을 받았습니당~");
        return ResponseEntity.ok(dataResponse);
    }

}
