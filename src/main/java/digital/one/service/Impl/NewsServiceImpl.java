package digital.one.service.Impl;

import digital.one.dto.PagingResponse;
import digital.one.model.News;
import digital.one.repository.NewsRepository;
import digital.one.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository repository;

    @Override
    public ResponseEntity<?> paging(int page, int size) {
        PageRequest of = PageRequest.of(page, size);
        Page<News> all = repository.findAll(of);
        return ResponseEntity.ok(new PagingResponse(all));
    }
}
