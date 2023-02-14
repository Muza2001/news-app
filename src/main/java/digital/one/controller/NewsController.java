package digital.one.controller;

import digital.one.dto.request.*;
import digital.one.service.NewsService;
import digital.one.utils.ApiPageable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


@RestController()
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @ApiPageable
    @GetMapping("/paging")
    public ResponseEntity<?> findAll(@RequestParam(required = false) String title,
                                     @RequestParam(required = false) String category_name,
                                     @ApiIgnore Pageable pageable){
        return ResponseEntity.status(200).body(newsService.findAllPagination(title, pageable, category_name));
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
