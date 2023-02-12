package digital.one.dto.response;

import digital.one.model.ImageData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsSimpleResponse {

    private Long id;

    private String title;

    private String description;

    private ImageDataResponse imageDataResponse;

    private Instant created_at;

    private Instant updated_at;

    private List<CategoryResponse> categoryResponse = new ArrayList<>();

}
