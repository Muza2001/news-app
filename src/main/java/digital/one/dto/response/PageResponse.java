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
public class PageResponse {

    private Long totalElements;

    private List<NewsResponse> content = new ArrayList<>();

    private Integer size;

    private Integer totalPages;

    private Integer pageNumber;

    private Integer getNumberOfElements;

}
