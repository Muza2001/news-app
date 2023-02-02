package digital.one.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class News {

    public News(String title, String description, Instant created_at, Instant updated_at, String imageUrl) {
        this.title = title;
        this.description = description;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.imageUrl = imageUrl;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private Instant created_at;

    private Instant updated_at;

    private String imageUrl;

   // private Map<String, String> messageList = new HashMap<>();

}
