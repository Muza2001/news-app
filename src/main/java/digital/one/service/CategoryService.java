package digital.one.service;

import digital.one.dto.request.CategoryRequest;
import org.springframework.http.ResponseEntity;

public interface CategoryService {
    ResponseEntity<?> deleteById(Long id);

    ResponseEntity<?> findById(Long id);

    ResponseEntity<?> create(CategoryRequest request);

    ResponseEntity<?> editById(Long id, CategoryRequest request);

    ResponseEntity<?> findAll();

}
