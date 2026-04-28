package com.task_service.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskServiceExceptionTest {

    @Test
    void constructor_ShouldCreateExceptionWithMessage() {
        String message = "Test exception message";
        TaskServiceException exception = new TaskServiceException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void constructor_ShouldHandleNullMessage() {
        TaskServiceException exception = new TaskServiceException(null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
    }
}