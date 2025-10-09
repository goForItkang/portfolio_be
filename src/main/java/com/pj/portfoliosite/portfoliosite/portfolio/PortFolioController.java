package com.pj.portfoliosite.portfoliosite.portfolio;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.dto.PageDTO;
import com.pj.portfoliosite.portfoliosite.portfolio.dto.ReqPortfolioDTO;
import com.pj.portfoliosite.portfoliosite.portfolio.dto.ResPortFolioDTO;
import com.pj.portfoliosite.portfoliosite.portfolio.dto.ResPortfolioDetailDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class PortFolioController {
    private final PortFolioService
            portfolioService;
    @PostMapping(value = "/portfolio",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "포트폴리오 저장",
        description = "header에 jwt 넣어주세요"
    )
    public ResponseEntity<DataResponse> portfolioUpload(
            ReqPortfolioDTO reqPortfolioDTO
    ) throws IOException {
        try{
            log.warn("portpolio upload : " + reqPortfolioDTO);
            Long id = portfolioService.save(reqPortfolioDTO);
            DataResponse dataResponse = new DataResponse();
            dataResponse.setData(id);
            System.out.println("데이터 전을 받았습니당~");

            return ResponseEntity.ok(dataResponse);
        }catch (Exception e){
            e.printStackTrace();
        return null;
        }
    }
    @GetMapping("/portfolio")
    @Operation(
            summary = "포트폴리오 가져오기",
            description = "id 기준으로 포트 폴리오가져오기"
    )
    public ResponseEntity<DataResponse> portfolioGetById(

        @RequestParam Long id
    ){
        log.info("portfolio {}",id);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setData(portfolioService.getPortFolio(id));

        return ResponseEntity.ok(dataResponse);
    }
    @GetMapping("/portfolio/{id}/details")
    @Operation(
            summary = "포트폴리오 세부정보(좋아요 및 북마크 상태)",
            description = "id로 세부 정보"
    )
   public ResponseEntity<DataResponse> portfolioGetDetailsById(
           @PathVariable Long id
    ){
        ResPortfolioDetailDTO portfolioDetailDTO = portfolioService.getPortFolioDetails(id);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setData(portfolioDetailDTO);
        return ResponseEntity.ok(dataResponse);
    }
    @GetMapping("/portfolio/recommend")
    @Operation(
            summary = "추천 포트폴리오 ",
            description = "4개 출력"
    )
    public ResponseEntity<DataResponse> portfolioRecommend(){
        DataResponse dataResponse = new DataResponse();
        dataResponse.setData(portfolioService.getPortFolioRecommend());
        dataResponse.setStatus(200);
        return ResponseEntity.ok(dataResponse);
    }
    @DeleteMapping("/portfolio")
    public ResponseEntity<DataResponse> portfolioDelete(
            @RequestParam Long id
    ){
        DataResponse dataResponse = new DataResponse();
        portfolioService.deletePortfolio(id);
        return ResponseEntity.ok(dataResponse);
    }
    // 기준 날짜 포트폴리오 가져오기
    @GetMapping("/portfolios/all")
    public ResponseEntity<PageDTO<ResPortFolioDTO>> portfolioGetAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ){

        return ResponseEntity.ok(portfolioService.getAll(page,size));
    }
    @PutMapping(value = "/portfolio/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "포트폴리오 수정 ",description = "header에 jwt 넣어주세요 ")
    public ResponseEntity<DataResponse> portfolioUpdate(
            @PathVariable Long id,
            ReqPortfolioDTO reqPortfolioDTO
    ) throws IOException {
        log.info("reqPortfolioDTO : " + reqPortfolioDTO);
        DataResponse dataResponse = new DataResponse();

        boolean result =portfolioService.update(id,reqPortfolioDTO);
        if(result){
            dataResponse.setStatus(200);
            dataResponse.setMessage("변경 성공");
        }else{
            dataResponse.setStatus(400);
            dataResponse.setMessage("변경 실패");
        }
        return ResponseEntity.ok(dataResponse);
    }


}
