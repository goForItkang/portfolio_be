package com.pj.portfoliosite.portfoliosite.global.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DataResponse<T> {
    private int status;
    private T data;

    /*
     * 데이터가 성공적으로 보내거나 보내지면 , 이 클래스에서 데이터를 담아서 보내야한다.
     */
}
