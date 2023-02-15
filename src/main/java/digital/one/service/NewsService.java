package digital.one.service;

import digital.one.dto.request.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface NewsService {

    ResponseEntity<?> create(NewsRequest request);


    ResponseEntity<?> findById(Long id);

    ResponseEntity<?> edit(NewsEditRequest request, Long id);

    ResponseEntity<?> deleteById(Long id);

    ResponseEntity<?> findAllPagination(String title, Pageable pageable, String category_name);

    ResponseEntity<?> isSelected();
}
