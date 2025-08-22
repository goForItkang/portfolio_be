package com.pj.portfoliosite.portfoliosite.global.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResProjectComment {
    private Long id;
    private String comment;
    private ResProjectComment projectComment; // 부모키 가져옴
    private boolean myself; // 본인 여부 확인
    private Long userId;
    private String userName;

}
