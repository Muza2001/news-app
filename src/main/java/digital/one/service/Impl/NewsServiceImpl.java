package digital.one.service.Impl;

import digital.one.dto.request.BasicInfoRequest;
import digital.one.dto.request.CategoryRequest;
import digital.one.dto.request.NewsEditRequest;
import digital.one.dto.request.NewsRequest;
import digital.one.dto.response.*;
import digital.one.model.BasicInformation;
import digital.one.model.Category;
import digital.one.model.News;
import digital.one.repository.BasicInformationRepository;
import digital.one.repository.CategoryRepository;
import digital.one.repository.NewsRepository;
import digital.one.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository repository;
    private final BasicInformationRepository basicInformationRepository;

    private final CategoryRepository categoryRepository;

    @Override
    public ResponseEntity<?> create(NewsRequest request) {
        News news = new News();
        NewsSimpleResponse newsSimpleResponse;
        Response response;
        if (request.getCategory_ids() != null){
            List<Category> categories = new ArrayList<>();
            for (Long l : request.getCategory_ids()) {
                if (l != null && l > 0){
                           categories.add(categoryRepository.findById(l).orElseThrow(()
                                    -> new IllegalArgumentException("Id not found" + l)));
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.builder()
                            .status_code(401)
                            .message("Category id not found " + l)
                            .success(false)
                            .build());
                }
            }
            news.setCategory(categories);
        }
        else {
            news.setCategory(null);
        }
            news.setCreated_at(Instant.now());
            news.setUpdated_at(Instant.now());
            news.setTitle(request.getTitle());
            news.setDescription(request.getDescription());
            news.setImageUrl(request.getImageUrl());
            News save = repository.save(news);
            if (news.getCategory() != null) {
                List<CategoryResponse> categoryResponses = new ArrayList<>();
                for (Category category : save.getCategory()) {
                    categoryResponses.add(new CategoryResponse(
                            category.getId(),
                            category.getName()));
                }

                newsSimpleResponse = NewsSimpleResponse.builder()
                        .updated_at(news.getUpdated_at())
                        .created_at(news.getCreated_at())
                        .categoryResponse(categoryResponses)
                        .description(news.getDescription())
                        .title(news.getTitle())
                        .id(save.getId())
                        .build();
            }
            else {
                newsSimpleResponse = NewsSimpleResponse.builder()
                        .updated_at(news.getUpdated_at())
                        .created_at(news.getCreated_at())
                        .categoryResponse(null)
                        .description(news.getDescription())
                        .title(news.getTitle())
                        .id(save.getId())
                        .build();
            }
        response = Response.builder()
                .success(true)
                .message("Successfully created")
                .data(newsSimpleResponse)
                .status_code(201)
                .build();
        return ResponseEntity.status(response.getStatus_code()).body(response);
    }

    @Override
    public ResponseEntity<?> findById(Long id) {
        Optional<News> optionalNews = repository.findById(id);
        Response response;
        NewsResponse newsResponse;
        if (!optionalNews.isPresent()){
            response = Response.builder()
                    .message("News not found")
                    .status_code(401)
                    .success(false)
                    .build();
        } else {
            News news = optionalNews.get();
            List<CategoryResponse> categoryResponse = new ArrayList<>();
            if (news.getCategory() == null) {
                categoryResponse = null;
            }
            else {
                for (Category category : news.getCategory()) {
                    categoryResponse.add(new CategoryResponse(category.getId(),category.getName()));
                }

            }
            Optional<List<BasicInformation>> optionalList = basicInformationRepository.findByNewsId(news.getId());
            if (!optionalList.isPresent()){
                newsResponse = NewsResponse.builder()
                        .categoryResponse(categoryResponse)
                        .infoResponses(null)
                        .title(news.getTitle())
                        .id(news.getId())
                        .imageUrl(news.getImageUrl())
                        .description(news.getDescription())
                        .build();
            }else {
                List<BasicInformation> basicInformations = optionalList.get();
                List<BasicInfoResponse> basicInfoResponses = new ArrayList<>();
                for (BasicInformation info : basicInformations){
                    basicInfoResponses.add( new BasicInfoResponse(
                            info.getId(),
                            info.getImageUrl(),
                            info.getMessage(),
                            info.getNews().getId()
                    ));
                }
                    newsResponse = NewsResponse.builder()
                            .categoryResponse(categoryResponse)
                            .infoResponses(basicInfoResponses)
                            .title(news.getTitle())
                            .id(news.getId())
                            .imageUrl(news.getImageUrl())
                            .description(news.getDescription())
                            .build();
            }
            response = Response.builder()
                    .success(true)
                    .status_code(200)
                    .data(newsResponse)
                    .message("News successfully find")
                    .build();
        }
        return ResponseEntity.status(response.getStatus_code()).body(response);
    }

    @Override
    public ResponseEntity<?> addInfoById(BasicInfoRequest requests, Long id) {
        Optional<News> optionalNews = repository.findById(id);
        Response response;
        if (!optionalNews.isPresent()){
            response = Response.builder()
                    .message("News not found")
                    .status_code(401)
                    .success(false)
                    .build();
        } else {
            News news = optionalNews.get();
            BasicInformation info = basicInformationRepository.save(BasicInformation.builder()
                    .imageUrl(requests.getImagerUrl())
                    .message(requests.getMessage())
                    .news(news)
                    .build());
            news.setUpdated_at(Instant.now());
            repository.save(news);
            response = Response.builder()
                    .success(true)
                    .status_code(200)
                    .data(news)
                    .message("News successfully updated and added information's")
                    .build();
        }
        return ResponseEntity.status(response.getStatus_code()).body(response);
    }

    @Override
    public ResponseEntity<?> edit(NewsEditRequest request, Long id) {
        Optional<News> optionalNews = repository.findById(id);
        Response response;
        NewsResponse newsResponse;
        if (!optionalNews.isPresent()){
            response = Response.builder()
                    .message("News id not found")
                    .status_code(401)
                    .success(false)
                    .data(null)
                    .build();
        }
        else {
            News news = optionalNews.get();
            List<Category> categories = new ArrayList<>();
            for (Long l : request.getCategory_ids()) {
                    categories.add(categoryRepository.findById(l)
                            .orElseThrow(() -> new IllegalArgumentException("Id not found")));
            }
            if (!news.getTitle().equals(request.getTitle()))
                news.setTitle(request.getTitle());

            if (!news.getDescription().equals(request.getDescription()))
                news.setDescription(request.getDescription());

            if (!news.getImageUrl().equals(request.getImageUrl()))
                news.setImageUrl(request.getImageUrl());

            news.setCategory(categories);
            Optional<List<BasicInformation>> optionalList = basicInformationRepository.findByNewsId(news.getId());
            List<BasicInfoResponse> basicInfoResponses = new ArrayList<>();
            if (!optionalList.isPresent()){
                basicInfoResponses = null;
            }
            else {
            List<BasicInformation> basicInformation = optionalList.get();
            for (BasicInformation info : basicInformation) {
                basicInfoResponses.add(new BasicInfoResponse(
                        info.getId(),
                        info.getImageUrl(),
                        info.getMessage(),
                        info.getNews().getId()
                ));
            }
        }

            List<CategoryResponse> categoryResponses = new ArrayList<>();
            for (Category c : categories) {
                categoryResponses.add(new CategoryResponse(c.getId(), c.getName()));
            }

            news.setUpdated_at(Instant.now());
            repository.save(news);
            newsResponse = NewsResponse.builder()
                    .title(news.getTitle())
                    .imageUrl(news.getImageUrl())
                    .infoResponses(basicInfoResponses)
                    .categoryResponse(categoryResponses)
                    .id(news.getId())
                    .build();
            response = Response.builder()
                    .data(newsResponse)
                    .success(true)
                    .status_code(200)
                    .message("News successfully edited")
                    .build();
        }
        return ResponseEntity.status(response.getStatus_code()).body(response);
    }

    @Override
    public ResponseEntity<?> findAllPagination(int page, int size) {
        PageRequest of = PageRequest.of(page, size);
        Page<News> newsPage = repository.findAll(of);
        Response response = Response.builder()
                .success(true)
                .data(newsPage)
                .message("Paging")
                .status_code(200)
                .build();
        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<?> addCategory(Long id, CategoryRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<?> deleteById(Long id) {
        Optional<News> optionalNews = repository.findById(id);
        Response response;
        if (!optionalNews.isPresent()){
            response = Response.builder()
                    .message("News id not found")
                    .success(false)
                    .data(null)
                    .status_code(401)
                    .build();
        }
        else {
            News news = optionalNews.get();
            basicInformationRepository.deleteById(news.getId());
            repository.deleteById(news.getId());
            response = Response.builder()
                    .status_code(200)
                    .message("News successfully deleted")
                    .success(true)
                    .build();
        }
        return ResponseEntity.status(response.getStatus_code()).body(response);
    }
}
