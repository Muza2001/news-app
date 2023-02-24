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
import digital.one.service.BasicInfoService;
import digital.one.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final BasicInfoServiceImpl basicInfoService;

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

        if (request.getDescription().length() > 500){
            response = Response.builder()
                    .message("Description length too long : " + request.getDescription().length() + " Max length 500")
                    .status_code(400)
                    .success(false)
                    .build();
            return ResponseEntity.status(400).body(response);
        }
        else {
            news.setDescription(request.getDescription());
        }
        if (request.getTitle().length() > 300){
            response = Response.builder()
                    .message("Title length too long" + request.getTitle().length() + "\n Max length 500")
                    .status_code(400)
                    .success(false)
                    .build();
            return ResponseEntity.status(400).body(response);
        }
        else {
            news.setTitle(request.getTitle());
        }
            news.setCreated_at(Instant.now());
            news.setUpdated_at(Instant.now());
            news.setSelected(request.isSelected());
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
        newsSimpleResponse = basicInfoService.getNewsSimpleResponse(save);
        response = Response.builder()
                .success(true)
                .message("Successfully created")
                .data(newsSimpleResponse)
                .status_code(201)
                .build();
        return ResponseEntity.status(response.getStatus_code()).body(response);
    }

    public NewsResponse getNewsResponse(News news){
        NewsResponse newsResponse;
        if (news != null){
            newsResponse = NewsResponse.builder()
                    .categoryResponse(basicInfoService.getCategoryResponse(news))
                    .infoResponses(null)
                    .title(news.getTitle())
                    .id(news.getId())
                    .isSelected(news.isSelected())
                    .imageDataResponse(getImageDataResponse(news.getImageData()))
                    .description(news.getDescription())
                    .build();
        }
        else {
            return null;
        }
        return newsResponse;
    }

    public NewsResponse getNewsResponse(News news,List<BasicInfoResponseWithoutNews> basicInfoResponseWithoutNews){
        NewsResponse newsResponse;
        if (news != null){
            newsResponse = NewsResponse.builder()
                    .categoryResponse(basicInfoService.getCategoryResponse(news))
                    .infoResponses(basicInfoResponseWithoutNews)
                    .title(news.getTitle())
                    .id(news.getId())
                    .isSelected(news.isSelected())
                    .imageDataResponse(getImageDataResponse(news.getImageData()))
                    .description(news.getDescription())
                    .build();
        }
        else {
            return null;
        }
        return newsResponse;
    }

    @Override
    public ResponseEntity<?> findById(Long id) {
        Optional<News> optionalNews = repository.findById(id);
        Response response;
        NewsResponse newsResponse;
        if (!optionalNews.isPresent()){
            response = Response.builder()
                    .message("News id not found")
                    .status_code(404)
                    .success(false)
                    .build();
        } else {
            News news = optionalNews.get();
            Optional<List<BasicInformation>> optionalList = basicInformationRepository.findByNewsOrderBySort_id(news);
            if (!optionalList.isPresent()){
                    newsResponse = getNewsResponse(news);
                }
            else {
                List<BasicInfoResponseWithoutNews> basicInfoResponseWithoutNews
                        = getBasicInfoResponseWithoutNews(optionalList.get());
                newsResponse = getNewsResponse(news,basicInfoResponseWithoutNews);
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

    public List<Category> getCategories(List<Long> category_ids){
        List<Category> categories = new ArrayList<>();
        boolean b = true;
        if (category_ids != null) {
            for (Long l : category_ids) {
                if (l != null && l > 0) {
                    Optional<Category> byId = categoryRepository.findById(l);
                    if (byId.isPresent()) {
                        categories.add(byId.get());
                    }
                    else {
                        b = false;
                        break;
                    }
                }
            }
        }
        if (!b){
            return null;
        }
        return categories;
    }

    @Override
    public ResponseEntity<?> edit(NewsEditRequest request, Long id) {
        Optional<News> optionalNews = repository.findById(id);
        Response response;
        NewsResponse newsResponse;
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
            List<Category> categories = getCategories(request.getCategory_ids());
            if (categories == null){
                response = Response.builder()
                        .message("There are wrong id please check category id list")
                        .success(false)
                        .status_code(400)
                        .build();
                return ResponseEntity.status(400).body(response);
            }
            if (!news.getCategory().equals(categories))
                news.setCategory(categories);

            if (!news.getDescription().equals(request.getDescription())) {
                if (request.getDescription().length() > 500) {
                    response = Response.builder()
                            .message("Description length too long" + request.getDescription().length() + "\n Max length 500")
                            .status_code(400)
                            .success(false)
                            .build();
                    return ResponseEntity.status(400).body(response);
                } else {
                    news.setDescription(request.getDescription());
                }
            }
            if (!news.getTitle().equals(request.getTitle())) {
                if (request.getTitle().length() > 300) {
                    response = Response.builder()
                            .message("Title length too long" + request.getTitle().length() + "\n Max length 500")
                            .status_code(400)
                            .success(false)
                            .build();
                    return ResponseEntity.status(400).body(response);
                } else {
                    news.setTitle(request.getTitle());
                }
            }


            if (news.isSelected() != request.isSelected())
                news.setSelected(request.isSelected());


            if (request.getImage_id() > 0 && request.getImage_id() != null){
                Optional<ImageData> byId = imageDataRepository.findById(request.getImage_id());
                if (byId.isPresent()){
                    ImageData imageData = byId.get();
                        news.setImageData(imageData);
                }
            }


            Optional<List<BasicInformation>> optionalList = basicInformationRepository.findAllByNews(news);
            if (optionalList.isPresent()) {
                newsResponse = getNewsResponse(news,getBasicInfoResponseWithoutNews(optionalList.get()));
            }
            else {
                newsResponse = getNewsResponse(news);
            }

            news.setUpdated_at(Instant.now());
            repository.save(news);
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
        if (basicInformation != null && basicInformation.size() > 0){
        for (BasicInformation info : basicInformation) {
            if (info != null) {
                basicInfoResponseWithoutNews.add(
                        BasicInfoResponseWithoutNews.builder()
                                .id(info.getId())
                                .message(info.getMessage())
                                .sort_id(info.getSort_id())
                                .imageDataResponse(getImageDataResponse(info.getImageData()))
                                .build());
            }
        }
    }
        else {
            return null;
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
                            .categoryResponse(basicInfoService.getCategoryResponse(news))
                            .id(news.getId())
                            .created_at(news.getCreated_at())
                            .build());
                }
            }
        }
        else {
            return null;
        }
        return newsResponses;
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
        PageResponse pageResponse;
        if (allByTitleContainsAndOrderByIdDesc != null && allByTitleContainsAndOrderByIdDesc.getSize() > 0) {
            pageResponse = PageResponse.builder()
                            .content(newsResponses(allByTitleContainsAndOrderByIdDesc.getContent()))
                            .pageNumber(allByTitleContainsAndOrderByIdDesc.getNumber())
                            .totalPages(allByTitleContainsAndOrderByIdDesc.getTotalPages())
                            .size(allByTitleContainsAndOrderByIdDesc.getSize())
                            .numberOfElements(allByTitleContainsAndOrderByIdDesc.getNumberOfElements())
                            .totalElements(allByTitleContainsAndOrderByIdDesc.getTotalElements())
                            .build();
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
            Optional<List<BasicInformation>> allByNews = basicInformationRepository.findAllByNews(news);
            allByNews.ifPresent(basicInformationRepository::deleteAll);
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
