package digital.one.dto.request;

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
public class NewsEditRequest {

    private String title;

    private String description;

    private Long image_id;

    private boolean isSelected = false;

    private List<Long> category_ids = new ArrayList<>();

}
