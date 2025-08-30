package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Entity
@Table(
        name = "portfolio_book_mark",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "portfolio_id"})
        }
)
public class PortFolioBookMark {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "portfolio_id", nullable = false)
    private PortFolio portfolio;

}
