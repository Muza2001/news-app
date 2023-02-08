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


    @PostMapping("/add_information_by_id/{id}")
    public ResponseEntity<?> add_info(@RequestBody BasicInfoRequest requests, @PathVariable Long id){
        return ResponseEntity.status(200).body(service.addInfoById(requests, id));
    }

    @GetMapping("/find_by_id/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        return ResponseEntity.status(200).body(service.findById(id));
    }

    @GetMapping("/find_by_news_id/{id}")
    public ResponseEntity<?> findByNewsId(@PathVariable Long id){
        return ResponseEntity.ok().body(service.findByNewsId(id));
    }

}
