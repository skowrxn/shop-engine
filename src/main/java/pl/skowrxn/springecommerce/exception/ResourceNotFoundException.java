package pl.skowrxn.springecommerce.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {

    private String objectName;
    private String fieldValue;
    private String fieldName;
    private Long id;
    private UUID uuid;

    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s not found with %s: %s", resourceName, fieldValue, fieldName));
        this.objectName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public ResourceNotFoundException(String resourceName, String fieldValue, Long id) {
        super(String.format("%s not found with %s: %s", resourceName, fieldValue, id));
        this.objectName = resourceName;
        this.id = id;
        this.fieldValue = fieldValue;
    }

    public ResourceNotFoundException(String resourceName, String fieldValue, UUID id) {
        super(String.format(resourceName + " not found with " + resourceName + ": " + id));
        this.objectName = resourceName;
        this.uuid = id;
        this.fieldValue = fieldValue;
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}