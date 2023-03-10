package digital.one.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicInfoRequest {

    private Long image_id;

    private Long news_id;

    private String message;

}
