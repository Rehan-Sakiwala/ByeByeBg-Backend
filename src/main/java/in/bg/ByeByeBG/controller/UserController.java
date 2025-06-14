package in.bg.ByeByeBG.controller;

import in.bg.ByeByeBG.dto.UserDto;
import in.bg.ByeByeBG.response.ByeByeBGResponse;
import in.bg.ByeByeBG.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createOrUpdateUser(@RequestBody UserDto userDto, Authentication authentication){
        ByeByeBGResponse response;
        try {
            if(!authentication.getName().equals(userDto.getClerkId())){
                System.out.println(authentication.getName());
                System.out.println(userDto.getClerkId());
                response = ByeByeBGResponse.builder()
                        .success(false)
                        .data("Unauthorised access")
                        .statusCode(HttpStatus.FORBIDDEN)
                        .build();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            UserDto user = userService.saveUser(userDto);
            response = ByeByeBGResponse.builder()
                    .success(true)
                    .data(user)
                    .statusCode(HttpStatus.OK)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch (Exception e){
            response = ByeByeBGResponse.builder()
                    .success(false)
                    .data(e.getMessage())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/credits")
    public ResponseEntity<?> getUserCredits(Authentication authentication){
        ByeByeBGResponse response = null;
        try{
          if(authentication.getName().isEmpty() || authentication.getName() == null){
              response = ByeByeBGResponse.builder()
                      .statusCode(HttpStatus.FORBIDDEN)
                      .data("User doesn't have permission/access to this resource.")
                      .success(false)
                      .build();
              return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
          }
          String clerkId = authentication.getName();
          UserDto existingUser = userService.getUserByClerkId(clerkId);
          Map<String, Integer> map = new HashMap<>();
          map.put("credits",existingUser.getCredits());
          response = ByeByeBGResponse.builder()
                  .statusCode(HttpStatus.OK)
                  .data(map)
                  .success(true)
                  .build();
          return ResponseEntity.status(HttpStatus.OK)
                  .body(response);
        }catch (Exception e){
            response = ByeByeBGResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data("Something went wrong!")
                    .success(false)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}
