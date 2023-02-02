package digital.one.service;

import org.springframework.http.ResponseEntity;

public interface NewsService {
    ResponseEntity<?> paging(int page, int size);
}
