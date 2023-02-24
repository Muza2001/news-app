package digital.one.repository;

import digital.one.model.BasicInformation;
import digital.one.model.ImageData;
import digital.one.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BasicInformationRepository extends JpaRepository<BasicInformation, Long> {

    Optional<List<BasicInformation>> findByNewsId(Long news_id);

    @Query(nativeQuery = true, value = "select * from basic_information bi inner join news n on bi.news_id = n.id where sort_id = ?1")
    Optional<BasicInformation> existsBySort_idOnBasicInfo(Long id);

    @Query(nativeQuery = true, value = "select *\n" +
            "from basic_information bi inner join news n on ?1 = bi.news_id order by bi.sort_id")
    Optional<List<BasicInformation>> findByNewsOrderBySort_id(News news);

    Optional<List<BasicInformation>> findAllByNews(News news);

    boolean existsByImageData(ImageData imageData);
}
