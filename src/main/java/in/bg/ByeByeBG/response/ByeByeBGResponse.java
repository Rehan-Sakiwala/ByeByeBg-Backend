package in.bg.ByeByeBG.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ByeByeBGResponse {
    private boolean success;
    private Object data;
    private HttpStatus statusCode;
}
