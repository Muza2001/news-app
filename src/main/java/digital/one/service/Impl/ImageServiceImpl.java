package digital.one.service.Impl;

import digital.one.dto.response.ImageDataResponse;
import digital.one.dto.response.Response;
import digital.one.model.BasicInformation;
import digital.one.model.ImageData;
import digital.one.model.News;
import digital.one.repository.BasicInformationRepository;
import digital.one.repository.ImageDataRepository;
import digital.one.repository.NewsRepository;
import digital.one.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageDataRepository repository;
    private final BasicInformationRepository basicInformationRepository;

    private final NewsRepository newsRepository;

    @Override
    public ResponseEntity<?> uploadImage(MultipartFile multipartFile) throws IOException {

        Response response;
        ImageDataResponse imageDataResponse;

        String originalFilename = multipartFile.getOriginalFilename();

        if (originalFilename != null && originalFilename.contains("..")) {
            response = Response.builder()
                    .message("Wrong file or data")
                    .success(false)
                    .data(null)
                    .status_code(404)
                    .build();
        } else if (repository.existsByOriginalName(originalFilename)) {
            response = Response.builder()
                    .message("File name already exists this database")
                    .success(false)
                    .data(null)
                    .status_code(404)
                    .build();

        } else {
            ImageData save = repository.save(new ImageData(
                    multipartFile.getName(),
                    originalFilename,
                    multipartFile.getContentType(),
                    multipartFile.getSize(),
                    multipartFile.getBytes(),
                    Instant.now(),
                    Instant.now()
            ));
            imageDataResponse = ImageDataResponse.builder()
                    .id(save.getId())
                    .data(save.getData())
                    .originalName(save.getOriginalName())
                    .contentType(save.getContentType())
                    .size(save.getSize())
                    .name(save.getName())
                    .created_at(save.getCreated_at())
                    .build();
            response = Response.builder()
                    .data(imageDataResponse)
                    .status_code(201)
                    .success(true)
                    .message("Image successfully saved")
                    .build();
        }
        return ResponseEntity.status(response.getStatus_code()).body(response);
    }

    @Override
    public ResponseEntity<?> edit(Long id, MultipartFile file) throws IOException {
        Optional<ImageData> byId = repository.findById(id);
        Response response;
        ImageDataResponse imageDataResponse;
        if (byId.isPresent()){
            ImageData imageData = byId.get();
            imageData.setData(file.getBytes());
            imageData.setName(file.getName());
            imageData.setSize(file.getSize());
            imageData.setContentType(file.getContentType());
            imageData.setCreated_at(imageData.getCreated_at());
            imageData.setUpdated_at(Instant.now());
            imageData.setOriginalName(file.getOriginalFilename());
            ImageData save = repository.save(imageData);
            imageDataResponse = ImageDataResponse
                    .builder()
                    .id(save.getId())
                    .data(save.getData())
                    .name(save.getName())
                    .originalName(save.getOriginalName())
                    .size(save.getSize())
                    .created_at(save.getCreated_at())
                    .contentType(save.getContentType())
                    .build();
            response = Response.builder()
                    .status_code(200)
                    .success(true)
                    .message("Image successfully edited")
                    .data(imageDataResponse)
                    .build();
        }
        else {
            response = Response.builder()
                .status_code(404)
                .success(false)
                .message("Image successfully edited")
                .build();
        }
        return ResponseEntity.status(response.getStatus_code()).body(response);
    }

    @Override
    public ResponseEntity<?> findById(Long id) {
        Optional<ImageData> byId = repository.findById(id);
        Response response;
        ImageDataResponse imageDataResponse;
        if (byId.isPresent()){
            ImageData save = byId.get();
            imageDataResponse = ImageDataResponse.builder()
                    .id(save.getId())
                    .data(save.getData())
                    .originalName(save.getOriginalName())
                    .size(save.getSize())
                    .contentType(save.getContentType())
                    .created_at(save.getCreated_at())
                    .name(save.getName())
                    .build();
            response = Response.builder()
                    .message("Image successfully find")
                    .success(true)
                    .status_code(200)
                    .data(imageDataResponse)
                    .build();
        }
        else {
            response = Response.builder()
                    .message("Image id not found")
                    .success(true)
                    .status_code(404)
                    .data(null)
                    .build();
        }
        return ResponseEntity.status(response.getStatus_code()).body(response);
    }

    @Override
    public ResponseEntity<?> delete(Long id) {
        Optional<ImageData> byId = repository.findById(id);
        Response response;
        if (byId.isPresent()) {
            ImageData imageData = byId.get();
                if (basicInformationRepository.existsByImageData(imageData)) {
                    return ResponseEntity.status(400).body(Response.builder()
                            .status_code(400)
                            .message("This image already used please first delete basic info related to this image")
                            .success(false)
                            .build());
                }

                if (newsRepository.existsByImageData(imageData)) {
                    return ResponseEntity.status(400).body(Response.builder()
                            .status_code(400)
                            .message("This image already used please first delete news related to this image")
                            .success(false)
                            .build());
                    }

                repository.delete(imageData);
                response = Response.builder()
                        .success(true)
                        .message("Image data successfully deleted")
                        .status_code(200)
                        .build();
            }

        else {
            response = Response.builder()
                    .status_code(404)
                    .message("Image data id not found")
                    .success(false)
                    .build();
        }
        return ResponseEntity.status(response.getStatus_code()).body(response);
    }
}
