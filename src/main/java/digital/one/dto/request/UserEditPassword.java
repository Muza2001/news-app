package digital.one.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEditPassword {

    private String confirm_password;

    private String new_password;

    private String retry_password;

}
