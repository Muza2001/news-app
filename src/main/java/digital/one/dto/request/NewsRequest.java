package digital.one.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsRequest {

    private String title;

    private String description;

    private String imageUrl;

    private Long category_id;
}
