package digital.one.controller;

import digital.one.dto.request.BasicInfoRequest;
import digital.one.dto.request.NewsEditRequest;
import digital.one.dto.request.NewsRequest;
import digital.one.model.News;
import digital.one.repository.NewsRepository;
import digital.one.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;
    private final NewsRepository repository;


    @GetMapping("/paging")
    public Page<News> findAll(@RequestParam int page, @RequestParam int size){
        PageRequest of = PageRequest.of(page, size);
        return repository.findAll(of);
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

    @PostMapping("/add_information_by_id/{id}")
    public ResponseEntity<?> add_info(@RequestBody BasicInfoRequest requests, @PathVariable Long id){
            return ResponseEntity.status(200).body(newsService.addInfoById(requests, id));
    }
}
