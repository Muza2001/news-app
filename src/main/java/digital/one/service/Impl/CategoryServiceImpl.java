package digital.one.service.Impl;

import digital.one.dto.request.CategoryRequest;
import digital.one.dto.response.CategoryResponse;
import digital.one.dto.response.Response;
import digital.one.model.Category;
import digital.one.model.News;
import digital.one.repository.CategoryRepository;
import digital.one.repository.NewsRepository;
import digital.one.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    private final NewsRepository newsRepository;



    @Override
    public ResponseEntity<?> deleteById(Long id) {
        Optional<Category> optionalCategory = repository.findById(id);
        Response response = null;
        if (!optionalCategory.isPresent()){
            response = Response.builder()
                    .success(false)
                    .data(null)
                    .message("Category id not found")
                    .status_code(401)
                    .build();
        }
        else {
            Category category = optionalCategory.get();
            for (News news : newsRepository.findAll()) {
                if (news != null) {
                    if (category.equals(news.getCategory())){
                        response = Response.builder()
                                .status_code(401)
                                .message("This category already used please first delete news related to this category")
                                .data(null)
                                .success(false)
                                .build();
                        break;
                    }
                }
            }
            assert response != null;
            if (!response.getStatus_code().equals(401)) {
                response = Response.builder()
                        .success(true)
                        .message("Successfully deleted")
                        .status_code(200)
                        .build();
            }
        }
        return ResponseEntity.status(response.getStatus_code()).body(response);
    }

    @Override
    public ResponseEntity<?> editById(Long id, CategoryRequest request) {
        Optional<Category> optionalCategory = repository.findById(id);
        Response response;
        CategoryResponse categoryResponse;
        if (!optionalCategory.isPresent()){
            response = Response.builder()
                    .success(false)
                    .data(null)
                    .message("Category id not found")
                    .status_code(401)
                    .build();
        }
        else {
            Category category = optionalCategory.get();

            if (!category.getName().equals(request.getName()))
                category.setName(request.getName());

            Category save = repository.save(category);
            categoryResponse = CategoryResponse.builder()
                    .id(save.getId())
                    .name(save.getName())
                    .build();
            response = Response.builder()
                    .status_code(201)
                    .message("Category successfully edited")
                    .data(categoryResponse)
                    .success(true)
                    .build();
        }
        return ResponseEntity.status(response.getStatus_code()).body(response);
    }

    @Override
    public ResponseEntity<?> create(CategoryRequest request) {
        Category category = new Category();
        Response response;
        CategoryResponse categoryResponse;
        boolean byName = repository.existsByName(request.getName().toUpperCase());
        if (byName){
            response = Response.builder()
                    .message("Category name already exists")
                    .success(false)
                    .status_code(401)
                    .build();
        }
        else {
            category.setName(request.getName().toUpperCase());
            Category save = repository.save(category);
            categoryResponse = CategoryResponse.builder()
                    .id(save.getId())
                    .name(save.getName())
                    .build();
            response = Response.builder()
                    .status_code(201)
                    .message("Category successfully created")
                    .data(categoryResponse)
                    .success(true)
                    .build();
        }
        return ResponseEntity.status(response.getStatus_code()).body(response);
    }

    @Override
    public ResponseEntity<?> getById(Long id) {
        Optional<Category> optionalCategory = repository.findById(id);
        Response response = null;
        CategoryResponse categoryResponse;
        if (!optionalCategory.isPresent()){
            response = Response.builder()
                    .success(false)
                    .data(null)
                    .message("Category id not found")
                    .status_code(401)
                    .build();
        }
        else {
            Category category = optionalCategory.get();
            categoryResponse = CategoryResponse.builder()
                    .name(category.getName())
                    .id(category.getId())
                    .build();
            response = Response.builder()
                    .status_code(200)
                    .message("Category find")
                    .success(true)
                    .data(category).build();
        }
        return ResponseEntity.status(response.getStatus_code()).body(response);
    }
}
