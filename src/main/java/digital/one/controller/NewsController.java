package digital.one.controller;

import digital.one.dto.request.*;
import digital.one.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController()
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/paging")
    public ResponseEntity<?> findAll(@RequestParam int page, @RequestParam int size){;
        return ResponseEntity.status(200).body(newsService.findAllPagination(page,size));
    }

    @GetMapping("/searching/{title}")
    public ResponseEntity<?> searching(@PathVariable String title){;
        return ResponseEntity.status(200).body(newsService.searching(title));
    }

    @GetMapping("/find_by_id/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        return ResponseEntity.status(200).body(newsService.findById(id));
    }

    @PutMapping("/edit_by_id/{id}")
    public ResponseEntity<?> editById(@PathVariable Long id, @RequestBody NewsEditRequest request){
        return ResponseEntity.status(200).body(newsService.edit(request, id));
    }

    @DeleteMapping("/delete_ny_id/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        return ResponseEntity.status(200).body(newsService.deleteById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody NewsRequest request){
        return ResponseEntity.status(201).body(newsService.create(request));
    }

}
