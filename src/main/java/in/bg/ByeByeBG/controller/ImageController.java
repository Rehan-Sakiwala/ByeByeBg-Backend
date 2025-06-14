package in.bg.ByeByeBG.controller;

import in.bg.ByeByeBG.dto.UserDto;
import in.bg.ByeByeBG.response.ByeByeBGResponse;
import in.bg.ByeByeBG.service.RemoveBackgroundService;
import in.bg.ByeByeBG.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {
    private final RemoveBackgroundService removeBackgroundService;
    private final UserService userService;

    @PostMapping("/removebg")
    public ResponseEntity<?> removeBackground(@RequestParam("file")MultipartFile file,
                                              Authentication authentication){

        ByeByeBGResponse response;
        Map<String, Object> responseMap = new HashMap<>();
        try{
            //Validation for logged in
            if(authentication.getName().isEmpty() || authentication.getName() == null){
                response = ByeByeBGResponse.builder()
                        .statusCode(HttpStatus.FORBIDDEN)
                        .success(false)
                        .data("No permission!")
                        .build();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            UserDto userDto= userService.getUserByClerkId(authentication.getName());
            //Validation for low credits
            if(userDto.getCredits()==0){
                responseMap.put("message","No credit balance");
                responseMap.put("creditBalance",userDto.getCredits());
                response = ByeByeBGResponse.builder()
                        .statusCode(HttpStatus.OK)
                        .success(false)
                        .data(responseMap)
                        .build();
                return ResponseEntity.ok(response);
            }

            //Remove background service
            byte[] imageBytes = removeBackgroundService.removeBackground(file);
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            //Deducting credits
            userDto.setCredits(userDto.getCredits() - 1);
            userService.saveUser(userDto);

            return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(base64Image);
        }catch(Exception e){
            response = ByeByeBGResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .success(false)
                    .data("Something went wrong!")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
