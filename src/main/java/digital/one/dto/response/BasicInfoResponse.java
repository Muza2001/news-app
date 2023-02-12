package digital.one.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BasicInfoResponse {

    private Long id;

    private ImageDataResponse imageDataResponse;

    private String message;

    private NewsSimpleResponse newsResponse;

}
