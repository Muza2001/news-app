package digital.one.dto.response;

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
public class NewsResponse {

    private Long id;

    private String title;

    private String description;

    private ImageDataResponse imageDataResponse;

    private List<CategoryResponse> categoryResponse = new ArrayList<>();

    private List<BasicInfoResponseWithoutNews> infoResponses = new ArrayList<>();

    private Instant created_at;

}
