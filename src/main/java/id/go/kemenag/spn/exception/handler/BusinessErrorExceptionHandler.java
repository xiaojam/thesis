package id.go.kemenag.spn.exception.handler;

import id.go.kemenag.spn.dto.custom.BusinessErrorExceptionResponse;
import id.go.kemenag.spn.exception.BusinessErrorException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class BusinessErrorExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessErrorException.class)
    public ResponseEntity<BusinessErrorExceptionResponse> handle(BusinessErrorException ex) {
        return new ResponseEntity<>(
            new BusinessErrorExceptionResponse(ex.getStatusCode().value(), ex.getReason()),
            ex.getStatusCode()
        );
    }
}
