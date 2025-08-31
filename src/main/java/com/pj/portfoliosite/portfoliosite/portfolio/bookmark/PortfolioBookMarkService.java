package com.pj.portfoliosite.portfoliosite.portfolio.bookmark;

import com.pj.portfoliosite.portfoliosite.global.entity.PortFolio;
import com.pj.portfoliosite.portfoliosite.global.entity.PortFolioBookMark;
import com.pj.portfoliosite.portfoliosite.global.entity.PortFolioLike;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.portfolio.PortFolioRepository;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PortfolioBookMarkService {
    private final PortfolioBookMarkRepository portfolioBookMarkRepository;
    private final UserRepository userRepository;
    private final PortFolioRepository portFolioRepository;
    @Transactional
    public void portfolioBookMark(Long id) {
        String testLogin = "portfolio@naver.com";
        Optional<User> user = userRepository.findByEmail(testLogin);
        if(user.isPresent()){
            PortFolioBookMark portFolioBookMark = new PortFolioBookMark();
            portFolioBookMark.addUser(user.get());
            PortFolio portFolio = portFolioRepository.selectById(id);
            portFolioBookMark.addPortfolio(portFolio);
            portFolio.addPortFolioBookMark(portFolioBookMark);
        }
    }

    public void portfolioBookMarkDelete(Long id) {
        String testLogin = "portfolio@naver.com";
        Optional<User> user = userRepository.findByEmail(testLogin);
        if(user.isPresent()){
            portfolioBookMarkRepository.delectByPortFolioIdAndUserId(user.get().getId(),id);
        }
    }
}
