package com.pj.portfoliosite.portfoliosite.portfolio.like;

import com.pj.portfoliosite.portfoliosite.global.entity.PortFolio;
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
public class PortFolioLikeService {
    private final PortFolioLikeRepository portFolioLikeRepository;
    private final PortFolioRepository portFolioRepository;
    private final UserRepository userRepository;
    @Transactional
    public void portfolioLike(Long id) {
        String testLogin = "portfolio@naver.com";
        Optional<User> user = userRepository.findByEmail(testLogin);
        if(user.isPresent()){
            PortFolioLike portFolioLike = new PortFolioLike();
            portFolioLike.addUser(user.get());
            PortFolio portFolio = portFolioRepository.selectById(id);
            portFolioLike.addPortfolio(portFolio);
            portFolio.addPortFolioLike(portFolioLike);
        }
    }

    public void portfolioLikeDelete(Long id) {
        String testLogin = "portfolio@naver.com";
        Optional<User> user =userRepository.findByEmail(testLogin);
        if(user.isPresent()){
            portFolioLikeRepository.delectById(user.get().getId(),id);
        }
    }
}
