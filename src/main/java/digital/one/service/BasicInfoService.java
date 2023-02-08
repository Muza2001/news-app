package digital.one.service;

import digital.one.dto.request.BasicInfoRequest;
import org.springframework.http.ResponseEntity;

public interface BasicInfoService {

    ResponseEntity<?> addInfoById(BasicInfoRequest requests, Long id);

    ResponseEntity<?> findById(Long id);

    ResponseEntity<?> findByNewsId(Long id);
}
