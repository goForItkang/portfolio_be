package com.pj.portfoliosite.portfoliosite.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class PasswordChangeDto {
    
    @NotBlank(message = "현재 비밀번호는 필수입니다.")
    private String currentPassword;
    
    @NotBlank(message = "새 비밀번호는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$",
            message = "비밀번호는 8-16자, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String newPassword;
    
    @NotBlank(message = "새 비밀번호 확인은 필수입니다.")
    private String newPasswordConfirm;
}
