package no.ntnu.idatt210620217.giddapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Class for error responses
 * @version 1.0
 */
public class ErrorResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String error;
    private int status;
    private String message;

    /**
     * Constructor
     * @param status Status message of error
     * @param message Message of error
     */
    public ErrorResponse(HttpStatus status, String message) {
        timestamp = LocalDateTime.now();
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
    }

    /**
     * getters
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getError() {
        return error;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
