package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;


@Getter
@Entity
@Table(
        name = "portfolio_like",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "portfolio_id"})
        }
)
public class PortFolioLike {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "portfolio_id", nullable = false)
    private PortFolio portfolio;

    @CreatedDate
    private LocalDateTime createdAt;

    public void addUser(User user){
        this.user = user;
    }
    public void addPortfolio(PortFolio portfolio){
        this.portfolio = portfolio;
    }

}
