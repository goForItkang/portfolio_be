package com.pj.portfoliosite.portfoliosite.portfolio;

import com.pj.portfoliosite.portfoliosite.global.entity.Award;
import com.pj.portfoliosite.portfoliosite.global.entity.PortFolio;
import com.pj.portfoliosite.portfoliosite.portfolio.dto.ReqCareerDTO;
import com.pj.portfoliosite.portfoliosite.portfolio.dto.ReqPortfolioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortFolioService {
    private final PortFolioRepository pfRepository;
    // 저장 로직
    public void save(ReqPortfolioDTO reqPortfolioDTO) {
        PortFolio portfolio = new PortFolio();
        Award award = new Award();


        try {

            pfRepository.insert(portfolio);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // 포트폴리오 에서 award 부분
    private 
}
