package id.go.kemenag.spn.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BusinessErrorException extends ResponseStatusException {

    public BusinessErrorException(HttpStatus status, String reason) {
        super(status, reason);
    }
}
