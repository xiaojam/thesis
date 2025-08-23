package id.go.kemenag.spn.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BusinessErrorExceptionResponse {

    private int code;

    private String message;
}
