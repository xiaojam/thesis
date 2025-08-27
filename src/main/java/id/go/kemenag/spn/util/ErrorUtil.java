package id.go.kemenag.spn.util;

import id.go.kemenag.spn.exception.BusinessErrorException;
import id.go.kemenag.spn.exception.BusinessErrorsException;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class ErrorUtil {

    public static void throwError(String message, HttpStatus status) {
        throw new BusinessErrorException(status, message);
    }

    public static void throwErrors(String message, HttpStatus status, Map<String, String> errors) {
        throw new BusinessErrorsException(status, message, errors);
    }
}
