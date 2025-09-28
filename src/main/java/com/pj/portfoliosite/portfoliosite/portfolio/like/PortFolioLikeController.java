package com.pj.portfoliosite.portfoliosite.portfolio.like;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.portfolio.PortFolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PortFolioLikeController {
    private final PortFolioLikeService portfolioLikeService;
    @PostMapping("/portfolio/{id}/like")
    public ResponseEntity<DataResponse> portfolioLike(
            @PathVariable Long id
    ){
        DataResponse dataResponse = new DataResponse();
        dataResponse.setData(null);
        log.info("like id : " + id);
        dataResponse.setMessage("success");
        portfolioLikeService.portfolioLike(id);
        return ResponseEntity.ok(dataResponse);
    }
    @DeleteMapping("/portfolio/{id}/like")
    public ResponseEntity<DataResponse> deleteLike(
            @PathVariable Long id
    ){
        DataResponse dataResponse = new DataResponse();
        dataResponse.setData(null);

        dataResponse.setMessage("success");
        portfolioLikeService.portfolioLikeDelete(id);
        return ResponseEntity.ok(dataResponse);
    }

}
