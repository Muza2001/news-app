package digital.one.service;

import digital.one.dto.request.BasicInfoRequest;
import digital.one.dto.request.NewsEditRequest;
import digital.one.dto.request.NewsRequest;
import org.springframework.http.ResponseEntity;

public interface NewsService {

    ResponseEntity<?> create(NewsRequest request);

    ResponseEntity<?> addInfoById(BasicInfoRequest requests, Long id);

    ResponseEntity<?> findById(Long id);

    ResponseEntity<?> edit(NewsEditRequest request, Long id);

    ResponseEntity<?> deleteById(Long id);
}
