package digital.one.repository;

import digital.one.model.ImageData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageDataRepository extends JpaRepository<ImageData,Long> {

    Optional<ImageData> findByName(String name);

    boolean existsByOriginalName(String originalName);
}
