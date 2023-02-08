package digital.one.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String imageUrl;

    private List<CategoryResponse> categoryResponse = new ArrayList<>();

    private List<BasicInfoResponseWithoutNews> infoResponses = new ArrayList<>();

}
