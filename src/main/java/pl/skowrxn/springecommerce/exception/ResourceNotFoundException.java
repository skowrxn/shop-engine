package pl.skowrxn.springecommerce.exception;

public class ResourceNotFoundException extends RuntimeException {

    private String objectName;
    private String fieldValue;
    private String fieldName;
    private Long id;

    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s not found with %s: %s", resourceName, fieldValue, fieldName));
        this.objectName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public ResourceNotFoundException(String resourceName, String fieldValue, Long id) {
        super(String.format("%s not found with %s: %d", resourceName, fieldValue, id));
        this.objectName = resourceName;
        this.id = id;
        this.fieldValue = fieldValue;
    }




}
