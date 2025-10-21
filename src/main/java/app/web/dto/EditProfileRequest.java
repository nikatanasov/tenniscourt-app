package app.web.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditProfileRequest {

    @Size(min = 6, message = "First name must be at least 6 symbols!")
    private String firstName;

    @Size(min = 6, message = "Last name must be at least 6 symbols!")
    private String lastName;

    @Size(min = 6, message = "Username must be at least 6 symbols!")
    private String username;

    @Email
    private String email;

    @URL
    private String profilePicture;

    @Size(min = 6, message = "Password must be at least 6 symbols!")
    private String password;
}
