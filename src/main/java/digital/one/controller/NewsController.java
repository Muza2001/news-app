package digital.one.controller;

import digital.one.model.News;
import digital.one.repository.NewsRepository;
import digital.one.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

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
}
