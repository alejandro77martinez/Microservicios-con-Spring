package com.task_service.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

class BadRequestExceptionTest {

    @Test
    void constructor_ShouldCreateExceptionWithMessage() {
        String message = "Bad request error";
        BadRequestException exception = new BadRequestException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void constructor_ShouldHandleNullMessage() {
        BadRequestException exception = new BadRequestException(null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    void shouldHaveCorrectResponseStatus() {
        ResponseStatus annotation = BadRequestException.class.getAnnotation(ResponseStatus.class);
        assertNotNull(annotation);
        assertEquals(HttpStatus.BAD_REQUEST, annotation.value());
    }
}