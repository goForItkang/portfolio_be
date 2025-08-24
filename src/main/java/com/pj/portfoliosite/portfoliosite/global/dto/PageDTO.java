package com.pj.portfoliosite.portfoliosite.global.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//페이지 사용할때 생성되는 DTO
// 페이지가 필요한 데이터 일 경우 dto 사용할 공용 dto
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO<T> {
    private List<T> content;

    private int page;               // 0-base
    private int size;               // 페이지당 개수
    private long totalElements;     // 전체 개수
    private int totalPages;         // 전체 페이지 수

    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;

    private int count;              // 이번 페이지 content 개수

}
