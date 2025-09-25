package com.pj.portfoliosite.portfoliosite.global.entity;

import com.pj.portfoliosite.portfoliosite.portfolio.dto.ReqPortfolioDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PortFolio {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String email;
    private String industry; // 분야
    private String jobPosition; //직무
    private String skill; // 스킬
    @Lob
    private String introductions; // 본인 소개
    private String thumbnailURL;

    private LocalDateTime createAt; // 작성일
    // 임시 저장
    private boolean saveStatus; // 저장 상태 임시저장/사용자에게 보여줄건지
    //문서 파트 수정해야함
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "portfolio",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Career> careers = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Award> awards = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Certificate> certificates = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Education> educations = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<PortFolioLike> portFolioLikes= new ArrayList<>();

    @OneToMany(mappedBy = "portfolio",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<PortFolioBookMark> portFolioBookMarks= new ArrayList<>();

    @OneToMany(mappedBy = "portfolio",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<PortfolioComment> portfolioComments= new ArrayList<>();

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<ProjectDescription> projectDescriptions = new ArrayList<>();

    public void save(ReqPortfolioDTO req){
        title = req.getTitle();
        email = req.getEmail();
        industry = req.getIndustry();
        jobPosition = req.getJobPosition();
//        skill = req.getSkill();
        introductions = req.getIntroductions();
        saveStatus = req.isSaveStatus(); // 임시 저장인지 아닌지 확인
        createAt = LocalDateTime.now();
    }
    public void addUser(User user){
        this.user = user;
    }
    public void addCareer(List<Career> careerList){
        for (Career c : careerList) {
            c.setPortfolio(this);
            this.careers.add(c);
        }
    }
    public void addAward(List<Award> awardList){
        for (Award a : awardList) {
            a.setPortfolio(this);
            this.awards.add(a);
        }
    }
    public void addCertificate(List<Certificate> certificateList){
        for (Certificate cert : certificateList) {
            cert.setPortfolio(this);
            this.certificates.add(cert);
        }
    }
    public void addEducation(List<Education> educationList){
        for (Education edu : educationList) {
            edu.setPortfolio(this);
            this.educations.add(edu);
        }
    }
    public void addPortFolioLike(PortFolioLike portFolioLike){
        this.portFolioLikes.add(portFolioLike);
    }
    public void addPortFolioBookMark(PortFolioBookMark portFolioBookMark){
        this.portFolioBookMarks.add(portFolioBookMark);
    }
    public void addProjectDescription(List<ProjectDescription> projectDescriptions){
        for(ProjectDescription pd : projectDescriptions){
            pd.addPortfolio(this);
            this.projectDescriptions.add(pd);

        }
    }
    public void addPortfolioFile(String fileName){
        this.thumbnailURL = fileName;
    }

}
