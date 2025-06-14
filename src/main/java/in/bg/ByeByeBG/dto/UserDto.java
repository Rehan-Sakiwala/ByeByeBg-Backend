package in.bg.ByeByeBG.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String clerkId;
    private String email;
    private String firstName;
    private String lastName;
    private String photoUrl;
    private Integer credits;
}
