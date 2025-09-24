package com.pj.portfoliosite.portfoliosite.teampost.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
public class FlexibleLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    private static final DateTimeFormatter[] FORMATTERS = {
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ISO_LOCAL_DATE_TIME
    };

    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) 
            throws IOException {
        String dateString = jsonParser.getText();
        
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        
        dateString = dateString.trim();
        
        // DateTime 형식인 경우 시간 부분 제거
        if (dateString.contains("T")) {
            dateString = dateString.split("T")[0];
        } else if (dateString.contains(" ")) {
            dateString = dateString.split(" ")[0];
        }
        
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(dateString);
            } catch (DateTimeParseException ex) {
                throw new IOException("날짜 형식을 파싱할 수 없습니다: " + dateString);
            }
        }
    }
}
