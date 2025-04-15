package pl.skowrxn.springecommerce.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {

    private int status;
    private String message;

    public ErrorResponse(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
    }

}
