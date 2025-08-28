package com.pj.portfoliosite.portfoliosite.portfolio;

import com.pj.portfoliosite.portfoliosite.global.entity.*;
import com.pj.portfoliosite.portfoliosite.portfolio.dto.*;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PortFolioService {
    private final PortFolioRepository pfRepository;
    private final UserRepository userRepository;
    // 저장 로직
    public void save(ReqPortfolioDTO reqPortfolioDTO) {
        //user part
        String testLogin = "portfolio@naver.com";
        Optional<User> user = userRepository.findByEmail(testLogin);

        // user 로직

        PortFolio portfolio = new PortFolio();
        portfolio.addUser(user.get());
        // award list
        portfolio.addAward(toAwardList(reqPortfolioDTO.getAwards()));
        // career list
        portfolio.addCareer(toCareerList(reqPortfolioDTO.getCareers()));
        // education list
        portfolio.addEducation(toEducationList(reqPortfolioDTO.getEducations()));
        // certificate list
        portfolio.addCertificate(toCertificateList(reqPortfolioDTO.getCertificates()));
        portfolio.save(reqPortfolioDTO);
        pfRepository.insert(portfolio);
    }
    // award list 형 변환
    private List<Award> toAwardList(List<ReqAwardDTO> reqAwardDTOList) {
        List<Award> awardList = new ArrayList<>();
        for (ReqAwardDTO reqAwardDTO : reqAwardDTOList) {
            Award award = new Award();
            award.addReqAwardDTO(reqAwardDTO);
            awardList.add(award);
        }

        return awardList;
    }
    // 커리어 리스트 db에 값으로 변환
    private List<Career> toCareerList(List<ReqCareerDTO> reqCareerDTOList) {
        List<Career> careerList = new ArrayList<>();
        for (ReqCareerDTO reqCareerDTO : reqCareerDTOList) {
            Career career = new Career();
            career.ReqCareerDTO(reqCareerDTO);
            careerList.add(career);
        }
        return careerList;
    }
    // education 리스트
    private List<Education> toEducationList(List<ReqEducationDTO> reqEducationDTOList ) {
        List<Education> educationList = new ArrayList<>();
        for (ReqEducationDTO reqEducationDTO : reqEducationDTOList) {
            Education education = new Education();
            education.ReqEducationDTO(reqEducationDTO);
            educationList.add(education);
        }
        return educationList;
    }
    // certificate 리스트
    private List<Certificate> toCertificateList(List<ReqCertificateDTO> reqCertificateDTOList) {
        List<Certificate> certificateList = new ArrayList<>();
        for (ReqCertificateDTO reqCertificateDTO : reqCertificateDTOList) {
            Certificate certificate = new Certificate();
            certificate.reqCertificateDTO(reqCertificateDTO);
            certificateList.add(certificate);
        }
        return certificateList;
    }

}
