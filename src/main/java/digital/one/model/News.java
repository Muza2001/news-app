package digital.one.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.Instant;
import java.util.*;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class News {

    public News(String title,
                String description,
                Instant created_at,
                Instant updated_at,
                ImageData imageData) {
        this.title = title;
        this.description = description;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.imageData = imageData;
    }

    public News(String title, String description, Instant created_at, Instant updated_at) {
        this.title = title;
        this.description = description;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 300)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    private Instant created_at;

    private Instant updated_at;


    private boolean isSelected;

    @ManyToOne
    private ImageData imageData;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Category> category = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        News news = (News) o;
        return id != null && Objects.equals(id, news.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
