package digital.one.service.Impl;

import digital.one.dto.request.*;
import digital.one.dto.response.*;
import digital.one.model.BasicInformation;
import digital.one.model.Category;
import digital.one.model.ImageData;
import digital.one.model.News;
import digital.one.repository.BasicInformationRepository;
import digital.one.repository.CategoryRepository;
import digital.one.repository.ImageDataRepository;
import digital.one.repository.NewsRepository;
import digital.one.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository repository;

    private final BasicInformationRepository basicInformationRepository;

    private final CategoryRepository categoryRepository;

    private final ImageDataRepository imageDataRepository;

    @Override
    public ResponseEntity<?> create(NewsRequest request) {
        News news = new News();
        NewsSimpleResponse newsSimpleResponse;
        Response response;
        List<Category> categories = new ArrayList<>();
        if (request.getCategory_ids() != null){
            for (Long l : request.getCategory_ids()) {
                if (l != null && l > 0){
                    Optional<Category> byId = categoryRepository.findById(l);
                    byId.ifPresent(categories::add);
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
            if (request.getImage_id() > 0 && request.getImage_id() != null){
                Optional<ImageData> byId = imageDataRepository.findById(request.getImage_id());
                if (byId.isPresent()) {
                    news.setImageData(byId.get());
                }
                else {
                    news.setImageData(null);
                }
            }
        News save = repository.save(news);
        if (news.getCategory() != null) {
                List<CategoryResponse> categoryResponses = new ArrayList<>();
                for (Category category : categories) {
                    categoryResponses.add(new CategoryResponse(
                            category.getId(),
                            category.getName()));
                }
                ImageDataResponse imageDataResponse = null;
                if (news.getImageData() != null){
                    ImageData imageData = news.getImageData();
                    imageDataResponse = ImageDataResponse.builder()
                            .id(imageData.getId())
                            .name(imageData.getName())
                            .data(imageData.getData())
                            .contentType(imageData.getContentType())
                            .size(imageData.getSize())
                            .originalName(imageData.getOriginalName())
                            .created_at(imageData.getCreated_at())
                            .updated_at(imageData.getUpdated_at())
                            .build();
                }
                newsSimpleResponse = NewsSimpleResponse.builder()
                        .updated_at(news.getUpdated_at())
                        .created_at(news.getCreated_at())
                        .imageDataResponse(imageDataResponse)
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
        ImageDataResponse imageDataResponse = null;
        if (!optionalNews.isPresent()){
            response = Response.builder()
                    .message("News not found")
                    .status_code(404)
                    .success(false)
                    .build();
        } else {
            News news = optionalNews.get();
            ImageData imageData = news.getImageData();
            if (imageData != null) {
                imageDataResponse = ImageDataResponse.builder()
                        .id(imageData.getId())
                        .name(imageData.getName())
                        .data(imageData.getData())
                        .contentType(imageData.getContentType())
                        .size(imageData.getSize())
                        .originalName(imageData.getOriginalName())
                        .created_at(imageData.getCreated_at())
                        .updated_at(imageData.getUpdated_at())
                        .build();
            }

            List<CategoryResponse> categoryResponse = new ArrayList<>();
            if (news.getCategory() == null) {
                categoryResponse = null;
            }
            else {
                for (Category category : news.getCategory()) {
                    if (category != null && category.getId() > 0) {
                        categoryResponse.add(new CategoryResponse(category.getId(), category.getName()));
                    }
                }
            }
            Optional<List<BasicInformation>> optionalList = basicInformationRepository.findByNewsId(news.getId());
            if (!optionalList.isPresent()){
                    newsResponse = NewsResponse.builder()
                            .categoryResponse(categoryResponse)
                            .infoResponses(null)
                            .title(news.getTitle())
                            .id(news.getId())
                            .imageDataResponse(imageDataResponse)
                            .description(news.getDescription())
                            .build();
                }
            else {
                List<BasicInformation> basicInformations = optionalList.get();
                List<BasicInfoResponseWithoutNews> basicInfoResponseWithoutNews
                        = getBasicInfoResponseWithoutNews(basicInformations);
                newsResponse = NewsResponse.builder()
                            .categoryResponse(categoryResponse)
                            .infoResponses(basicInfoResponseWithoutNews)
                            .title(news.getTitle())
                            .id(news.getId())
                            .imageDataResponse(imageDataResponse)
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
    public ResponseEntity<?> edit(NewsEditRequest request, Long id) {
        Optional<News> optionalNews = repository.findById(id);
        Response response;
        NewsResponse newsResponse;
        ImageDataResponse imageDataResponse = null;
        if (!optionalNews.isPresent()){
            response = Response.builder()
                    .message("News id not found")
                    .status_code(404)
                    .success(false)
                    .data(null)
                    .build();
        }
        else {
            News news = optionalNews.get();
            List<Category> categories = new ArrayList<>();
            if (request.getCategory_ids() != null) {
            for (Long l : request.getCategory_ids()) {
                if (l != null && l > 0) {
                    categories.add(categoryRepository.findById(l)
                            .orElseThrow(() -> new IllegalArgumentException("Id not found")));
                    }
                }
            }
            if (!news.getTitle().equals(request.getTitle()))
                news.setTitle(request.getTitle());

            if (!news.getDescription().equals(request.getDescription()))
                news.setDescription(request.getDescription());

            if (request.getImage_id() > 0 && request.getImage_id() != null){
                Optional<ImageData> byId = imageDataRepository.findById(request.getImage_id());
                if (byId.isPresent()){
                    ImageData imageData = byId.get();
                        news.setImageData(imageData);
                        imageDataResponse = ImageDataResponse.builder()
                                .id(imageData.getId())
                                .data(imageData.getData())
                                .updated_at(Instant.now())
                                .created_at(imageData.getCreated_at())
                                .name(imageData.getName())
                                .contentType(imageData.getContentType())
                                .originalName(imageData.getOriginalName())
                                .size(imageData.getSize())
                                .build();
                }
            }

            news.setCategory(categories);
            Optional<List<BasicInformation>> optionalList = basicInformationRepository.findByNewsId(news.getId());
            List<BasicInfoResponseWithoutNews> basicInfoResponseWithoutNews;
            if (optionalList.isPresent()) {
                List<BasicInformation> basicInformation = optionalList.get();
                basicInfoResponseWithoutNews = getBasicInfoResponseWithoutNews(basicInformation);
            }
            else {
                basicInfoResponseWithoutNews = null;
            }

            List<CategoryResponse> categoryResponses = new ArrayList<>();
            for (Category c : categories) {
                categoryResponses.add(new CategoryResponse(c.getId(), c.getName()));
            }

            news.setUpdated_at(Instant.now());
            repository.save(news);
            newsResponse = NewsResponse.builder()
                    .title(news.getTitle())
                    .imageDataResponse(imageDataResponse)
                    .infoResponses(basicInfoResponseWithoutNews)
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

    public List<BasicInfoResponseWithoutNews> getBasicInfoResponseWithoutNews
            (List<BasicInformation> basicInformation){
        List<BasicInfoResponseWithoutNews> basicInfoResponseWithoutNews = new ArrayList<>();
        for (BasicInformation info : basicInformation) {
            if (info != null) {
                basicInfoResponseWithoutNews.add(
                        BasicInfoResponseWithoutNews.builder()
                                .id(info.getId())
                                .message(info.getMessage())
                                .imageDataResponse(ImageDataResponse.builder()
                                        .contentType(info.getImageData().getContentType())
                                        .size(info.getImageData().getSize())
                                        .originalName(info.getImageData().getOriginalName())
                                        .name(info.getImageData().getName())
                                        .created_at(info.getImageData().getCreated_at())
                                        .updated_at(info.getImageData().getUpdated_at())
                                        .data(info.getImageData().getData())
                                        .id(info.getImageData().getId())
                                        .build())
                                .build());
            }
        }
        return basicInfoResponseWithoutNews;
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
    public ResponseEntity<?> deleteById(Long id) {
        Optional<News> optionalNews = repository.findById(id);
        Response response;
        if (!optionalNews.isPresent()){
            response = Response.builder()
                    .message("News id not found")
                    .success(false)
                    .data(null)
                    .status_code(404)
                    .build();
        }
        else {
            News news = optionalNews.get();
            basicInformationRepository.deleteByNewsId(news.getId());
            repository.deleteById(news.getId());
            response = Response.builder()
                    .status_code(200)
                    .message("News successfully deleted")
                    .success(true)
                    .build();
        }
        return ResponseEntity.status(response.getStatus_code()).body(response);
    }

    @Override
    public ResponseEntity<?> searching(String title) {
        Optional<List<News>> byTitleContains = repository.findNewsByTitleContaining(title);
        Response response;
        if (byTitleContains.isPresent()) {
            List<News> news = byTitleContains.get();
            response = Response.builder()
                    .data(Collections.singletonList(news))
                    .message("Searching result")
                    .status_code(200)
                    .success(true)
                    .build();
        }
        else {
            response = Response.builder()
                    .message("Something wrong")
                    .status_code(404)
                    .success(false)
                    .build();
        }
        return ResponseEntity.status(response.getStatus_code()).body(response);
    }
}
