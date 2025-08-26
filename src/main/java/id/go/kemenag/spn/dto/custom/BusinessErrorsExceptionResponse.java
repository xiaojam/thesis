package id.go.kemenag.spn.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class BusinessErrorsExceptionResponse {

    private Integer code;

    private String message;

    private Map<String, String> errors;
}
