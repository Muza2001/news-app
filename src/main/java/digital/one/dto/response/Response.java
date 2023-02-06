package digital.one.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response {

    public Response(String message, boolean success, Integer status_code, Object data) {
        this.message = message;
        this.success = success;
        this.status_code = status_code;
        this.data = data;
    }

    public Response(String message, boolean success, Integer status_code, Set<Object> objects) {
        this.message = message;
        this.success = success;
        this.status_code = status_code;
        this.objects = objects;
    }

    private String message;
    private boolean success;
    private Integer status_code;
    private Object data;
    private Set<Object> objects = new HashSet<>();


}
