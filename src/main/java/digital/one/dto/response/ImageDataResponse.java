package digital.one.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ImageDataResponse {

    private Long id;

    private String name;

    private String originalName;

    private String contentType;

    private Long size;

    private byte[] data;

    private Instant created_at;

}
