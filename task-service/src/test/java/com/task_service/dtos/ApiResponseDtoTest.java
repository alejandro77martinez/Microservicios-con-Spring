package com.task_service.dtos;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseDtoTest {

    @Test
    void builder_ShouldCreateValidDto() {
        Date timestamp = new Date();

        ApiResponseDto dto = ApiResponseDto.builder()
                .status(200)
                .message("Success message")
                .timestamp(timestamp)
                .build();

        assertNotNull(dto);
        assertEquals(200, dto.getStatus());
        assertEquals("Success message", dto.getMessage());
        assertEquals(timestamp, dto.getTimestamp());
    }

    @Test
    void builder_ShouldHandleNullValues() {
        ApiResponseDto dto = ApiResponseDto.builder()
                .status(null)
                .message(null)
                .timestamp(null)
                .build();

        assertNotNull(dto);
        assertNull(dto.getStatus());
        assertNull(dto.getMessage());
        assertNull(dto.getTimestamp());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyDto() {
        ApiResponseDto dto = new ApiResponseDto(null, null, null);

        assertNotNull(dto);
        assertNull(dto.getStatus());
        assertNull(dto.getMessage());
        assertNull(dto.getTimestamp());
    }

    @Test
    void allArgsConstructor_ShouldCreateValidDto() {
        Date timestamp = new Date();
        ApiResponseDto dto = new ApiResponseDto(404, "Not found", timestamp);

        assertNotNull(dto);
        assertEquals(404, dto.getStatus());
        assertEquals("Not found", dto.getMessage());
        assertEquals(timestamp, dto.getTimestamp());
    }
}