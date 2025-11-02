package com.pj.portfoliosite.portfoliosite.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

/**
 * 비밀번호 재설정 요청 DTO (이메일 참조)
 * POST /api/user/reset-password
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDto {
    
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;
    
    @NotBlank(message = "새 비밀번호는 필수입니다.")
    private String newPassword;
    
    @NotBlank(message = "새 비밀번호 확인은 필수입니다.")
    private String newPasswordConfirm;
}
