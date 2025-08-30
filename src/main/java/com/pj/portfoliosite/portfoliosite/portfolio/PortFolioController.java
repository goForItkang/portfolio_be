package com.pj.portfoliosite.portfoliosite.portfolio;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.portfolio.dto.ReqPortfolioDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/portfolio/{id}")
    @Operation(
            summary = "포트폴리오 가져오기",
            description = "id 기준으로 포트 폴리오가져오기"
    )
    public ResponseEntity<DataResponse> portfolioGetById(
        @PathVariable Long id
    ){
        DataResponse dataResponse = new DataResponse();
        dataResponse.setData(portfolioService.getPortFolio(id));

        return ResponseEntity.ok(dataResponse);
    }

}
