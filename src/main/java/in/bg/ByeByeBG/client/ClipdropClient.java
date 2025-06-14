package in.bg.ByeByeBG.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "clipdropclient", url = "https://clipdrop-api.co")
public interface ClipdropClient {

    @PostMapping(value = "/remove-background/v1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    byte[] removeBackground(@RequestPart("image_file")MultipartFile file,
                     @RequestHeader("x-api-key")String apikey);
}
