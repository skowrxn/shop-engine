package pl.skowrxn.springecommerce.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    public String uploadImage(MultipartFile image);

}
