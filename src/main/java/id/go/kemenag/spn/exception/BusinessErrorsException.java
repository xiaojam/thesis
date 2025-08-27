package id.go.kemenag.spn.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

public class BusinessErrorsException extends ResponseStatusException {

    private final Map<String, String> errors;

    public BusinessErrorsException(HttpStatusCode status, String reason, Map<String, String> errors) {
        super(status, reason);
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
