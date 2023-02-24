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

        if (requests.getMessage().length() > 90000){
            response = Response.builder()
                    .success(false)
                    .status_code(400)
                    .message("Message too long " + requests.getMessage() + "\n Message max length 90000")
                    .build();
        }
        else if (!optionalNews.isPresent()){
            response = Response.builder()
                    .message("News id not found")
                    .status_code(404)
                    .success(false)
                    .build();
        } else {
            ImageData basicInfoImageData = byId.orElse(null);
            News news = optionalNews.get();
            if (requests.getSort_id() != null && requests.getSort_id() > 0){
                Optional<BasicInformation> basicInformation = repository.existsBySort_idOnBasicInfo(news,requests.getSort_id());
                if (basicInformation.isPresent()){
                    return ResponseEntity.status(400).body(Response.builder()
                            .message("Sort id already exists or sort id wrong")
                            .status_code(404)
                            .success(false)
                            .build());
                }
            }

            BasicInformation info = repository.save(BasicInformation.builder()
                    .imageData(basicInfoImageData)
                    .message(requests.getMessage())
                    .news(news)
                    .sort_id(requests.getSort_id())
                    .build());

            news.setUpdated_at(Instant.now());

            BasicInfoResponse basicInfoResponse = getBasicInfoResponse(getNewsSimpleResponse(news), info);
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

            NewsSimpleResponse  newsSimpleResponse = getNewsSimpleResponse(news);

            infoResponse = getBasicInfoResponse(newsSimpleResponse,information);

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

    public BasicInfoResponse getBasicInfoResponse(NewsSimpleResponse newsSimpleResponse, BasicInformation information){
        BasicInfoResponse infoResponse;
        if (newsSimpleResponse != null && information != null){
            infoResponse = BasicInfoResponse.builder()
                    .newsResponse(newsSimpleResponse)
                    .id(information.getId())
                    .sort_id(information.getSort_id())
                    .message(information.getMessage())
                    .imageDataResponse(getImageDataResponse(information.getImageData()))
                    .build();
        }
        else {
            return null;
        }
        return infoResponse;
    }

    public List<CategoryResponse> getCategoryResponse(News news){
        List<CategoryResponse> categoryResponses = new ArrayList<>();
        if (news != null) {
            for (Category c : news.getCategory()) {
                if (c != null) {
                    categoryResponses.add(CategoryResponse.builder()
                            .id(c.getId())
                            .name(c.getName())
                            .build());
                }
            }
        }
        else {
            return null;
        }
        return categoryResponses;
    }

    public ImageDataResponse getImageDataResponse(ImageData imageData){
        ImageDataResponse imageDataResponse;
        if (imageData != null) {
            imageDataResponse = ImageDataResponse.builder().id(imageData.getId())
                    .data(imageData.getData())
                    .created_at(imageData.getCreated_at())
                    .name(imageData.getName())
                    .contentType(imageData.getContentType())
                    .originalName(imageData.getOriginalName())
                    .size(imageData.getSize())
                    .build();
        }
        else {
            return null;
        }
        return imageDataResponse;
    }

    public NewsSimpleResponse getNewsSimpleResponse(News news){
        NewsSimpleResponse newsSimpleResponse;
        if (news != null){
            newsSimpleResponse =  NewsSimpleResponse.builder()
                    .id(news.getId())
                    .title(news.getTitle())
                    .created_at(news.getCreated_at())
                    .description(news.getDescription())
                    .imageDataResponse(getImageDataResponse(news.getImageData()))
                    .categoryResponse(getCategoryResponse(news))
                    .build();
        }
        else {
            return null;
        }
        return newsSimpleResponse;
    }

    @Override
    public ResponseEntity<?> editById(Long id, BasicInfoRequest basicInfoRequest) {
        Optional<BasicInformation> byId = repository.findById(id);
        Response response;
        BasicInfoResponse basicInfoResponse;
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
            if (imageData == null) {
                if (basicInfoRequest.getImage_id() > 0 && basicInfoRequest.getImage_id() != null) {
                    Optional<ImageData> dataOptional = imageDataRepository.findById(basicInfoRequest.getImage_id());
                    if (dataOptional.isPresent())
                        information.setImageData(dataOptional.get());
                    else {
                        return ResponseEntity.status(404).body(Response.builder()
                                .success(false)
                                .message("Image id not found")
                                .build());
                    }
                }
            }
            else if(!imageData.getId().equals(basicInfoRequest.getImage_id())) {
                    Optional<ImageData> dataOptional = imageDataRepository.findById(basicInfoRequest.getImage_id());
                    if (dataOptional.isPresent()) {
                        information.setImageData(dataOptional.get());
                    } else {
                        return ResponseEntity.status(404).body(Response.builder()
                                .success(false).message("Image id not found").build());
                    }
            }

            if (!information.getSort_id().equals(basicInfoRequest.getSort_id())){
                if (basicInfoRequest.getSort_id() != null && basicInfoRequest.getSort_id() > 0){
                    Optional<BasicInformation> basicInformation =
                            repository.existsBySort_idOnBasicInfo(news,basicInfoRequest.getSort_id());
                    if (basicInformation.isPresent()) {
                        return ResponseEntity.status(400).body(Response.builder()
                                .success(false)
                                .message("Sort id already exists")
                                .status_code(400)
                                .build());
                    }
                    else {
                        information.setSort_id(basicInfoRequest.getSort_id());
                    }
                }
            }

            if (!information.getMessage().equals(basicInfoRequest.getMessage())) {
                information.setMessage(basicInfoRequest.getMessage());
            }

            if (!news.getId().equals(basicInfoRequest.getNews_id())) {
                Optional<News> newsRepositoryById = newsRepository.findById(basicInfoRequest.getNews_id());
                if (newsRepositoryById.isPresent()) {
                    News news1 = newsRepositoryById.get();
                    information.setNews(news1);
                } else {
                    return ResponseEntity.status(400).body(Response.builder()
                            .success(false).message("News id not found").build());
                }
            }

            BasicInformation save = repository.save(information);

            newsSimpleResponse = getNewsSimpleResponse(save.getNews());

            basicInfoResponse = getBasicInfoResponse(newsSimpleResponse,information);

            response = Response.builder()
                    .data(basicInfoResponse)
                    .message("Information successfully edited")
                    .success(true)
                    .status_code(200)
                    .build();

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
