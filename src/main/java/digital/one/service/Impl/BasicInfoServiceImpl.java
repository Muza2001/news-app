package digital.one.service.Impl;

import digital.one.dto.request.BasicInfoRequest;
import digital.one.dto.response.*;
import digital.one.model.BasicInformation;
import digital.one.model.Category;
import digital.one.model.ImageData;
import digital.one.model.News;
import digital.one.repository.BasicInformationRepository;
import digital.one.repository.ImageDataRepository;
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

    private final ImageDataRepository imageDataRepository;

    @Override
    public ResponseEntity<?> create(BasicInfoRequest requests) {
        Optional<News> optionalNews = newsRepository.findById(requests.getNews_id());
        Optional<ImageData> byId = imageDataRepository.findById(requests.getImage_id());
        Response response;
        if (!optionalNews.isPresent()){
            response = Response.builder()
                    .message("News id not found")
                    .status_code(401)
                    .success(false)
                    .build();
        } else if (!byId.isPresent()) {
            response = Response.builder()
                    .message("Image id not found")
                    .status_code(404)
                    .success(false)
                    .build();
        } else {
            News news = optionalNews.get();
            ImageData newsImageData = news.getImageData();
            ImageData basicInfoImageData = byId.get();
            BasicInformation info = repository.save(BasicInformation.builder()
                    .imageData(basicInfoImageData)
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
            ImageDataResponse newsImageDataResponse = ImageDataResponse.builder()
                    .id(newsImageData.getId())
                    .data(newsImageData.getData())
                    .created_at(newsImageData.getCreated_at())
                    .name(newsImageData.getName())
                    .contentType(newsImageData.getContentType())
                    .originalName(newsImageData.getOriginalName())
                    .size(newsImageData.getSize())
                    .build();

            ImageDataResponse basicInfoImageDataResponse = ImageDataResponse.builder()
                    .id(basicInfoImageData.getId())
                    .data(basicInfoImageData.getData())
                    .created_at(basicInfoImageData.getCreated_at())
                    .name(basicInfoImageData.getName())
                    .contentType(basicInfoImageData.getContentType())
                    .originalName(basicInfoImageData.getOriginalName())
                    .size(basicInfoImageData.getSize())
                    .build();
            news.setUpdated_at(Instant.now());

            BasicInfoResponse basicInfoResponse = BasicInfoResponse.builder()
                    .id(info.getId())
                    .newsResponse(NewsSimpleResponse.builder()
                            .id(news.getId())
                            .title(news.getTitle())
                            .imageDataResponse(newsImageDataResponse)
                            .description(news.getDescription())
                            .created_at(news.getCreated_at())
                            .categoryResponse(categoryResponses)
                            .build())
                    .message(info.getMessage())
                    .imageDataResponse(basicInfoImageDataResponse)
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
        ImageDataResponse newsImageDataResponse = null;
        ImageDataResponse infoImageDataResponse = null;
        if (byId.isPresent()){
            BasicInformation information = byId.get();
            News news = information.getNews();
            ImageData newsImageData = news.getImageData();
            List<CategoryResponse> categoryResponses = new ArrayList<>();
            for (Category c : news.getCategory()) {
                if (c != null){
                    categoryResponses.add(CategoryResponse.builder()
                            .id(c.getId())
                            .name(c.getName())
                            .build());
                }
            }

            if (news.getImageData() != null){
                newsImageDataResponse = ImageDataResponse.builder().id(newsImageData.getId())
                        .data(newsImageData.getData())
                        .created_at(newsImageData.getCreated_at())
                        .name(newsImageData.getName())
                        .contentType(newsImageData.getContentType())
                        .originalName(newsImageData.getOriginalName())
                        .size(newsImageData.getSize())
                        .build();
            }

            if (information.getImageData() != null){
                ImageData infoImageData = information.getImageData();
                infoImageDataResponse = ImageDataResponse.builder().id(infoImageData.getId())
                        .data(infoImageData.getData())
                        .created_at(infoImageData.getCreated_at())
                        .name(infoImageData.getName())
                        .contentType(infoImageData.getContentType())
                        .originalName(infoImageData.getOriginalName())
                        .size(infoImageData.getSize())
                        .build();
            }

            NewsSimpleResponse  newsSimpleResponse = NewsSimpleResponse.builder()
                    .id(news.getId())
                    .title(news.getTitle())
                    .created_at(news.getCreated_at())
                    .description(news.getDescription())
                    .imageDataResponse(newsImageDataResponse)
                    .categoryResponse(categoryResponses)
                    .build();
            infoResponse = BasicInfoResponse.builder()
                    .newsResponse(newsSimpleResponse)
                    .id(information.getId())
                    .message(information.getMessage())
                    .imageDataResponse(infoImageDataResponse)
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
                    .status_code(404)
                    .build();
        }
        return ResponseEntity.status(response.getStatus_code()).body(response);
    }

    @Override
    public ResponseEntity<?> editById(Long id, BasicInfoRequest basicInfoRequest) {
        Optional<BasicInformation> byId = repository.findById(id);
        Response response;
        BasicInfoResponse basicInfoResponse;
        ImageDataResponse imageNewsDataResponse = null;
        ImageDataResponse imageBasicInfoResponse = new ImageDataResponse();
        NewsSimpleResponse newsSimpleResponse;
        if (!byId.isPresent()) {
            response = Response.builder()
                    .status_code(404)
                    .success(false)
                    .message("Info id not found")
                    .build();
        } else {
            BasicInformation information = byId.get();
            News news = information.getNews();
            ImageData imageData = information.getImageData();
            if (!imageData.getId().equals(basicInfoRequest.getImage_id())) {
                Optional<ImageData> dataOptional = imageDataRepository.findById(basicInfoRequest.getImage_id());
                if (dataOptional.isPresent()) {
                    information.setImageData(dataOptional.get());
                } else {
                    return ResponseEntity.status(401).body(Response.builder()
                            .success(false).message("Image id not found"));
                }
            }
            List<CategoryResponse> categoryResponses = new ArrayList<>();
            if (!news.getId().equals(basicInfoRequest.getNews_id())) {
                Optional<News> newsRepositoryById = newsRepository.findById(basicInfoRequest.getNews_id());
                if (newsRepositoryById.isPresent()) {
                    News news1 = newsRepositoryById.get();
                    information.setNews(news1);
                    for (Category category : news1.getCategory()) {
                        if (category != null) {
                            categoryResponses.add(new CategoryResponse(category.getId(), category.getName()));
                        }
                    }
                } else {
                    return ResponseEntity.status(401).body(Response.builder()
                            .success(false).message("News id not found"));
                }

            }
            if (!information.getMessage().equals(basicInfoRequest.getMessage())) {
                information.setMessage(basicInfoRequest.getMessage());
            }
                News news1 = information.getNews();
            ImageData infoImageData = information.getImageData();
            ImageData newsImageData = news1.getImageData();
                if (newsImageData != null) {
                    imageNewsDataResponse = ImageDataResponse.builder()
                            .id(newsImageData.getId())
                            .data(newsImageData.getData())
                            .size(newsImageData.getSize())
                            .contentType(newsImageData.getContentType())
                            .created_at(newsImageData.getCreated_at())
                            .originalName(newsImageData.getOriginalName())
                            .name(newsImageData.getName())
                            .build();
                }
                if (infoImageData != null){
                    imageBasicInfoResponse = ImageDataResponse.builder()
                            .id(infoImageData.getId())
                            .data(infoImageData.getData())
                            .size(infoImageData.getSize())
                            .contentType(infoImageData.getContentType())
                            .created_at(infoImageData.getCreated_at())
                            .originalName(infoImageData.getOriginalName())
                            .name(infoImageData.getName())
                            .build();

                }
            newsSimpleResponse = NewsSimpleResponse.builder()
                    .categoryResponse(categoryResponses)
                    .imageDataResponse(imageNewsDataResponse)
                    .id(news1.getId())
                    .created_at(news1.getCreated_at())
                    .title(news1.getTitle())
                    .description(news1.getDescription())
                    .build();

            basicInfoResponse = BasicInfoResponse.builder()
                    .newsResponse(newsSimpleResponse)
                    .imageDataResponse(imageBasicInfoResponse)
                    .id(information.getId())
                    .build();

            response = Response.builder()
                    .data(basicInfoResponse)
                    .message("Information successfully edited")
                    .success(true)
                    .status_code(200)
                    .build();

            repository.save(information);
        }
        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<?> delete(Long id) {
        Optional<BasicInformation> byId = repository.findById(id);
        Response response;
        if (byId.isPresent()){
            BasicInformation information = byId.get();
            repository.delete(information);
            response = Response.builder()
                    .status_code(200)
                    .success(true)
                    .message("Info successfully deleted")
                    .build();
        }
        else {
            response = Response.builder()
                    .message("Info " + id + " not found")
                    .success(false)
                    .status_code(404)
                    .build();
        }
        return ResponseEntity.status(response.getStatus_code()).body(response);
    }
}
