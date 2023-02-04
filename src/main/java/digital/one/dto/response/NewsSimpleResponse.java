package digital.one.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsSimpleResponse {

    private Long id;

    private String title;

    private String description;

    private Instant created_at;

    private Instant updated_at;

    private CategoryResponse categoryResponse;

}
