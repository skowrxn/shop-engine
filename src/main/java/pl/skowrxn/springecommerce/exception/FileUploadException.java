package pl.skowrxn.springecommerce.exception;

public class FileUploadException extends RuntimeException {

    private String fileName;

    public FileUploadException(String fileName) {
        super("Could not upload file: " + fileName);
        this.fileName = fileName;
    }

}
