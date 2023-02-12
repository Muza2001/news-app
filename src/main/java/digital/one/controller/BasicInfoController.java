package digital.one.controller;

import digital.one.dto.request.BasicInfoRequest;
import digital.one.service.BasicInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/basic_info")
public class BasicInfoController {

    private final BasicInfoService service;


    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody BasicInfoRequest requests){
        return ResponseEntity.status(200).body(service.create(requests));
    }

    @GetMapping("/find_by_id/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        return ResponseEntity.status(200).body(service.findById(id));
    }

    @PutMapping("/edit/{basic_info_id}")
    public ResponseEntity<?> edit(@PathVariable(name = "basic_info_id") Long id,
                                  @RequestBody BasicInfoRequest basicInfoRequest){
        return ResponseEntity.status(200).body(service.editById(id, basicInfoRequest));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return ResponseEntity.status(200).body(service.delete(id));
    }

}
