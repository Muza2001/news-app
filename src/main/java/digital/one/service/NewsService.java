package digital.one.service;

import digital.one.dto.request.*;
import org.springframework.http.ResponseEntity;

public interface NewsService {

    ResponseEntity<?> create(NewsRequest request);


    ResponseEntity<?> findById(Long id);

    ResponseEntity<?> edit(NewsEditRequest request, Long id);

    ResponseEntity<?> deleteById(Long id);

    ResponseEntity<?> findAllPagination(int page, int size);


    ResponseEntity<?> searching(String title);
}
