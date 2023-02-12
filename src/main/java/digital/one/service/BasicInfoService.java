package digital.one.service;

import digital.one.dto.request.BasicInfoRequest;
import org.springframework.http.ResponseEntity;

public interface BasicInfoService {

    ResponseEntity<?> create(BasicInfoRequest requests);

    ResponseEntity<?> findById(Long id);

    ResponseEntity<?> editById(Long id, BasicInfoRequest basicInfoRequest);

    ResponseEntity<?> delete(Long id);
}
