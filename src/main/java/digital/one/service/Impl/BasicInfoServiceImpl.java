package digital.one.service.Impl;

import digital.one.dto.request.BasicInfoRequest;
import digital.one.dto.response.*;
import digital.one.model.BasicInformation;
import digital.one.model.Category;
import digital.one.model.News;
import digital.one.repository.BasicInformationRepository;
import digital.one.repository.NewsRepository;
import digital.one.service.BasicInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasicInfoServiceImpl implements BasicInfoService {

    private final BasicInformationRepository repository;
    private final NewsRepository newsRepository;

    @Override
    public ResponseEntity<?> addInfoById(BasicInfoRequest requests, Long id) {
        Optional<News> optionalNews = newsRepository.findById(id);
        Response response;
        if (!optionalNews.isPresent()){
            response = Response.builder()
                    .message("News not found")
                    .status_code(401)
                    .success(false)
                    .build();
        } else {
            News news = optionalNews.get();
            BasicInformation info = repository.save(BasicInformation.builder()
                    .imageUrl(requests.getImagerUrl())
                    .message(requests.getMessage())
                    .news(news)
                    .build());
            List<CategoryResponse> categoryResponses = new ArrayList<>();
            for (Category c : news.getCategory()) {
                if (c != null){
                    categoryResponses.add(CategoryResponse.builder()
                            .id(c.getId())
                            .name(c.getName())
                            .build());
                }
            }
            news.setUpdated_at(Instant.now());
            BasicInfoResponse basicInfoResponse = BasicInfoResponse.builder()
                    .id(info.getId())
                    .newsResponse(NewsSimpleResponse.builder()
                            .id(news.getId())
                            .title(news.getTitle())
                            .description(news.getDescription())
                            .updated_at(news.getUpdated_at())
                            .created_at(news.getCreated_at())
                            .categoryResponse(categoryResponses)
                            .build())
                    .imageUrl(info.getImageUrl())
                    .message(info.getMessage())
                    .build();
            newsRepository.save(news);
            response = Response.builder()
                    .success(true)
                    .status_code(200)
                    .data(basicInfoResponse)
                    .message("News successfully updated and added information's")
                    .build();
        }
        return ResponseEntity.status(response.getStatus_code()).body(response);
    }

    @Override
    public ResponseEntity<?> findById(Long id) {
        Optional<BasicInformation> byId = repository.findById(id);
        Response response;
        BasicInfoResponse infoResponse;
        if (byId.isPresent()){
            BasicInformation information = byId.get();
            News news = information.getNews();
            List<CategoryResponse> categoryResponses = new ArrayList<>();
            for (Category c : news.getCategory()) {
                if (c != null){
                    categoryResponses.add(CategoryResponse.builder()
                            .id(c.getId())
                            .name(c.getName())
                            .build());
                }
            }
            NewsSimpleResponse  newsSimpleResponse = NewsSimpleResponse.builder()
                    .id(news.getId())
                    .title(news.getTitle())
                    .updated_at(news.getUpdated_at())
                    .created_at(news.getCreated_at())
                    .description(news.getDescription())
                    .categoryResponse(categoryResponses)
                    .build();
            infoResponse = BasicInfoResponse.builder()
                    .newsResponse(newsSimpleResponse)
                    .id(information.getId())
                    .message(information.getMessage())
                    .imageUrl(information.getImageUrl())
                    .build();
            response = Response.builder()
                    .success(true)
                    .message("Basic information find")
                    .status_code(200)
                    .data(infoResponse)
                    .build();
        } else {
            response = Response.builder()
                    .success(false)
                    .message("Basic information id not found")
                    .status_code(401)
                    .build();
        }
        return ResponseEntity.status(response.getStatus_code()).body(response);
    }

    @Override
    public ResponseEntity<?> findByNewsId(Long id) {
        Optional<List<BasicInformation>> byNewsId = repository.findByNewsId(id);
        Optional<News> byId = newsRepository.findById(id);
        NewsResponse newsResponse;
        Response response;
        if (byNewsId.isPresent() && byId.isPresent()){
            List<BasicInfoResponseWithoutNews> withoutNews = new ArrayList<>();
            List<CategoryResponse> categoryResponses = new ArrayList<>();
            News news = byId.get();
            for (BasicInformation information : byNewsId.get()) {
                withoutNews.add(BasicInfoResponseWithoutNews.builder()
                        .id(information.getId())
                        .imageUrl(information.getImageUrl())
                        .message(information.getMessage())
                        .build());
            }
            for (Category ca: news.getCategory()) {
                if (ca != null && ca.getId() == 0) {
                    categoryResponses.add(CategoryResponse.builder()
                            .id(ca.getId())
                            .name(ca.getName())
                            .build());
                }
            }
            newsResponse = NewsResponse.builder()
                    .id(news.getId())
                    .infoResponses(withoutNews)
                    .description(news.getDescription())
                    .categoryResponse(categoryResponses)
                    .imageUrl(news.getImageUrl())
                    .build();
            response = Response.builder()
                    .data(newsResponse)
                    .status_code(200)
                    .message("News information's")
                    .success(true)
                    .build();
        }
        return null;
    }
}
