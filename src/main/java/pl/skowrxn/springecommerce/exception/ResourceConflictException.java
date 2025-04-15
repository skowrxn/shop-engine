package pl.skowrxn.springecommerce.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResourceConflictException extends RuntimeException {

    private String objectName;
    private String fieldName;
    private String value;

    public ResourceConflictException(String objectName, String fieldName, String value) {
        super(objectName + " with " + fieldName + "=" + value + " already exists.");
        this.objectName = objectName;
        this.fieldName = fieldName;
        this.value = value;
    }

    public ResourceConflictException(String objectName, String fieldName, Long value) {
        super(objectName + " with " + fieldName + "=" + value + " already exists.");
        this.objectName = objectName;
        this.fieldName = fieldName;
        this.value = String.valueOf(value);
    }

    public ResourceConflictException(String objectName) {
        super(objectName + " already exists.");
        this.objectName = objectName;
    }


}
