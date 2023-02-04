package digital.one.repository;

import digital.one.model.BasicInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BasicInformationRepository extends JpaRepository<BasicInformation, Long> {

    Optional<List<BasicInformation>> findByNewsId(Long news_id);

}
