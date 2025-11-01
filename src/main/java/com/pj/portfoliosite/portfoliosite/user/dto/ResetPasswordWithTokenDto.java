package com.pj.portfoliosite.portfoliosite.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordWithTokenDto {
    @NotBlank(message = "리셋 토큰은 필수입니다.")
    private String resetToken;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$",
            message = "비밀번호는 8-16자, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String newPassword;
}
