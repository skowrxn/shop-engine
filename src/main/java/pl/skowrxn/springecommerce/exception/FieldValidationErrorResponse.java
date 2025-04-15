package pl.skowrxn.springecommerce.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
public class FieldValidationErrorResponse {

    private int status;
    private Map<String, List<String>> errors;

    public FieldValidationErrorResponse(HttpStatus status, Map<String, List<String>> errors) {
        this.status = status.value();
        this.errors = errors;
    }

}
