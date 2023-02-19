package digital.one.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "original_name")
    private String originalName;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "content_size")
    private Long size;

    @Column(name = "data")
    private byte[] data;

    private Instant created_at;

    private Instant updated_at;

    public ImageData(String name,
                     String originalName,
                     String contentType,
                     Long size,
                     byte[] data,
                     Instant created_at,
                     Instant updated_at) {
        this.name = name;
        this.originalName = originalName;
        this.contentType = contentType;
        this.size = size;
        this.data = data;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ImageData imageData = (ImageData) o;
        return id != null && Objects.equals(id, imageData.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
