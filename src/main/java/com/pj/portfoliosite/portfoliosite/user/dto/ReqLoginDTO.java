package com.pj.portfoliosite.portfoliosite.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqLoginDTO {
    private String email;
    private String password;
}
