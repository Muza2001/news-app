package digital.one.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {

    ResponseEntity<?> uploadImage(MultipartFile multipartFile) throws IOException;

    ResponseEntity<?> downloadById(Long id);

    ResponseEntity<?> edit(Long id, MultipartFile file) throws IOException;

    ResponseEntity<?> delete(Long id);
}
