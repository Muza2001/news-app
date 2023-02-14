package digital.one.repository;

import digital.one.model.Category;
import digital.one.model.ImageData;
import digital.one.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    Page<News> findAllByCategoryOrderByIdDesc(Pageable pageable, Category category);

    @Query(nativeQuery = true, value = "select * from news order by news.id desc")
    Page<News> findAllByPaginationForSort(Pageable pageable);

    Page<News> findAllByTitleContainsOrderByIdDesc(String title,Pageable pageable);

    boolean existsByImageData(ImageData imageData);

}
