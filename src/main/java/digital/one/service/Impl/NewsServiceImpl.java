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
import org.springframework.data.domain.Pageable;
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
            news.setSelected(request.isSelected());
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
                    imageDataResponse = getImageDataResponse(imageData);
                }
                newsSimpleResponse = NewsSimpleResponse.builder()
                        .created_at(news.getCreated_at())
                        .imageDataResponse(imageDataResponse)
                        .categoryResponse(categoryResponses)
                        .isSelected(news.isSelected())
                        .description(news.getDescription())
                        .title(news.getTitle())
                        .id(save.getId())
                        .build();
            }
            else {
                newsSimpleResponse = NewsSimpleResponse.builder()
                        .created_at(news.getCreated_at())
                        .categoryResponse(null)
                        .description(news.getDescription())
                        .isSelected(news.isSelected())
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
                imageDataResponse = getImageDataResponse(imageData);
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
                            .isSelected(news.isSelected())
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
                            .isSelected(news.isSelected())
                            .created_at(news.getCreated_at())
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

            if (news.isSelected() != request.isSelected())
                news.setSelected(request.isSelected());

            if (!news.getDescription().equals(request.getDescription()))
                news.setDescription(request.getDescription());

            if (request.getImage_id() > 0 && request.getImage_id() != null){
                Optional<ImageData> byId = imageDataRepository.findById(request.getImage_id());
                if (byId.isPresent()){
                    ImageData imageData = byId.get();
                        news.setImageData(imageData);
                        imageDataResponse = getImageDataResponse(imageData);
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
                    .description(news.getDescription())
                    .imageDataResponse(imageDataResponse)
                    .infoResponses(basicInfoResponseWithoutNews)
                    .categoryResponse(categoryResponses)
                    .isSelected(news.isSelected())
                    .id(news.getId())
                    .created_at(news.getCreated_at())
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
                                .imageDataResponse(getImageDataResponse(info.getImageData()))
                                .build());
            }
        }
        return basicInfoResponseWithoutNews;
    }

    public List<NewsResponse> newsResponses(List<News> newsList){
        List<NewsResponse> newsResponses = new ArrayList<>();
        if (newsList.size() > 0){
            for (News news: newsList) {
                if (news != null){
                    newsResponses.add(NewsResponse.builder()
                            .title(news.getTitle())
                            .description(news.getDescription())
                            .imageDataResponse(getImageDataResponse(news.getImageData()))
                            .infoResponses(getBasicInfoResponse(news))
                            .isSelected(news.isSelected())
                            .categoryResponse(getCategoryResponse(news.getCategory()))
                            .id(news.getId())
                            .created_at(news.getCreated_at())
                            .build());
                }
            }
        }
        else {
            newsResponses = null;
        }
        return newsResponses;
    }

    private List<CategoryResponse> getCategoryResponse(List<Category> category) {
        List<CategoryResponse> categoryResponses = new ArrayList<>();
        if (category.size() > 0){
            for (Category ca: category) {
                if (ca != null){
                    categoryResponses.add(CategoryResponse.builder()
                            .id(ca.getId())
                            .name(ca.getName())
                            .build());
                }
            }
        }
        else {
            categoryResponses = null;
        }
        return categoryResponses;
    }

    private List<BasicInfoResponseWithoutNews> getBasicInfoResponse(News news) {
        Optional<List<BasicInformation>> byNewsId = basicInformationRepository.findByNewsId(news.getId());
        return byNewsId.map(this::getBasicInfoResponseWithoutNews).orElse(null);
    }

    private ImageDataResponse getImageDataResponse(ImageData imageData) {
        ImageDataResponse imageDataResponse;
        if (imageData != null){
            imageDataResponse = ImageDataResponse.builder()
                    .id(imageData.getId())
                    .data(imageData.getData())
                    .name(imageData.getName())
                    .originalName(imageData.getOriginalName())
                    .contentType(imageData.getContentType())
                    .size(imageData.getSize())
                    .created_at(imageData.getCreated_at())
                    .build();
        }
        else {
            imageDataResponse = null;
        }
        return imageDataResponse;
    }

    @Override
    public ResponseEntity<?> isSelected() {
        Optional<List<News>> byIsSelectedToTrueLimit10 = repository.findByIsSelectedToTrueLimit10();
        List<NewsResponse> newsResponses;
        Response response;
        if (byIsSelectedToTrueLimit10.isPresent()){
            newsResponses = newsResponses(byIsSelectedToTrueLimit10.get());
            response = Response.builder()
                    .status_code(200)
                    .message("Selected news")
                    .success(true)
                    .data(newsResponses)
                    .build();
        }
        else {
            response = Response.builder()
                    .data(null)
                    .status_code(200)
                    .message("Selected news not found")
                    .build();
        }
        return ResponseEntity.status(response.getStatus_code()).body(response);
    }

    @Override
    public ResponseEntity<?> findAllPagination(String title, Pageable pageable, String category_name) {
        Response response;
        PageResponse pageResponse;
        if (title == null && category_name == null){
            Page<News> allByPaginationForSort =
                    repository.findAllByPaginationForSort(pageable);
            pageResponse = pageResponse(allByPaginationForSort);
            response = Response.builder()
                    .data(pageResponse)
                    .success(true)
                    .message("Paging")
                    .status_code(200)
                    .build();

        } else if (title == null) {
            Optional<Category> byName = categoryRepository.findByName(category_name);
            if (byName.isPresent()){
                Category category = byName.get();
                Page<News> allByPagination = repository.findAllByCategoryOrderByIdDesc(pageable,category);
                pageResponse = pageResponse(allByPagination);
                response = Response.builder()
                        .data(pageResponse)
                        .status_code(200)
                        .success(true)
                        .message("Paging")
                        .build();
            }
            else {
                response = Response.builder()
                        .success(false)
                        .message("Category name not found")
                        .status_code(404)
                        .build();
            }
        }  else {
            Page<News> allByTitleContainsAndOrderByIdDesc =
                    repository.findAllSearchingByTitleAndDescription(title, pageable);
            pageResponse = pageResponse(allByTitleContainsAndOrderByIdDesc);
            response = Response.builder()
                    .data(pageResponse)
                    .success(true)
                    .message("Paging")
                    .status_code(200)
                    .build();
        }
        return ResponseEntity.status(response.getStatus_code()).body(response);
    }

    public PageResponse pageResponse(Page<News> allByTitleContainsAndOrderByIdDesc){
        PageResponse pageResponse = new PageResponse();
        if (allByTitleContainsAndOrderByIdDesc != null && allByTitleContainsAndOrderByIdDesc.getSize() > 0) {
            pageResponse.setContent(newsResponses(allByTitleContainsAndOrderByIdDesc.getContent()));
            pageResponse.setPageNumber(allByTitleContainsAndOrderByIdDesc.getNumber());
            pageResponse.setTotalPages(allByTitleContainsAndOrderByIdDesc.getTotalPages());
            pageResponse.setSize(allByTitleContainsAndOrderByIdDesc.getSize());
            pageResponse.setNumberOfElements(allByTitleContainsAndOrderByIdDesc.getNumberOfElements());
            pageResponse.setTotalElements(allByTitleContainsAndOrderByIdDesc.getTotalElements());
        }
        else {
            pageResponse = null;
        }
        return pageResponse;
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
}
