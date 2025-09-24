package com.pj.portfoliosite.portfoliosite.mypage;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.entity.PortFolio;
import com.pj.portfoliosite.portfoliosite.portfolio.PortFolioRepository;
import com.pj.portfoliosite.portfoliosite.portfolio.PortFolioService;
import com.pj.portfoliosite.portfoliosite.portfolio.dto.ResPortFolioDTO;
import com.pj.portfoliosite.portfoliosite.portfolio.dto.ResPortfolioDetailDTO;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import com.pj.portfoliosite.portfoliosite.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final PortFolioRepository  portfolioRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PortFolioService portfolioService;
    public DataResponse getPortfolio() {
        DataResponse dataResponse = new DataResponse(); //객체 생성
        String email =  SecurityContextHolder.getContext().getAuthentication().getName();
        if(email == null){
            dataResponse.setData(null);
            dataResponse.setStatus(401);
            dataResponse.setMessage("로그인이 필요합니다. ");
        return dataResponse;
        }else if(userRepository.findByEmail(email).isPresent()){
            List<PortFolio> portFolioList = portfolioRepository.selectByUserEmail(email);
            if(portFolioList.isEmpty()){
                dataResponse.setData(null);
                dataResponse.setStatus(404);
                dataResponse.setMessage("메세지가 없음");
                return dataResponse;
            }else{
                List<ResPortFolioDTO> res = portfolioService.portfolioDTOTOEntity(portFolioList);
                dataResponse.setData(res);
                dataResponse.setStatus(200);
                return dataResponse;
            }
        }
        return null;
    }

}
