package digital.one.controller;

import digital.one.dto.request.CategoryRequest;
import digital.one.service.CategoryService;
import digital.one.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CategoryRequest request){
        return ResponseEntity.status(201).body(service.create(request));
    }

    @PostMapping("/add_category/{id}")
    public ResponseEntity<?> addCategory(@PathVariable Long id, @RequestBody CategoryRequest request){
        return ResponseEntity.status(200).body(service.addCategory(id,request));
    }

    @PutMapping("/edit_by_id/{id}")
    public ResponseEntity<?> editById(@PathVariable Long id, CategoryRequest request){
        return ResponseEntity.status(200).body(service.editById(id,request));
    }

    @GetMapping("/get_by_id/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        return ResponseEntity.status(200).body(service.getById(id));
    }

    @DeleteMapping("/delete_by_id/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        return ResponseEntity.status(200).body(service.deleteById(id));
    }

}
