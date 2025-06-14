package in.bg.ByeByeBG.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.bg.ByeByeBG.dto.UserDto;
import in.bg.ByeByeBG.entity.UserEntity;
import in.bg.ByeByeBG.response.ByeByeBGResponse;
import in.bg.ByeByeBG.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class ClerkWebhookController {
        @Value("${clerk.webhook.secret}")
        private String webhookSecret;

        private final UserService userService;

        @PostMapping("/clerk")
        public ResponseEntity<?> handleClerkWebhook(@RequestHeader("svix-id") String svixid, @RequestHeader("svix-timestamp") String svixTimestamp, @RequestHeader("svix-signature") String svixSignature, @RequestBody String payload){
            ByeByeBGResponse response = null;
            try{
                boolean isValid = verifyWebhookSignature(svixid,svixTimestamp,svixSignature,payload);
                if(!isValid){
                    response = ByeByeBGResponse.builder()
                            .statusCode(HttpStatus.UNAUTHORIZED)
                            .data("Invalid Webhook signature")
                            .success(false)
                            .build();
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(response);
                }
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(payload);
                String eventType = rootNode.path("type").asText();
                switch(eventType){
                    case "user.created":
                        handleUserCreated(rootNode.path("data"));
                        break;
                    case "user.updated":
                        handleUserUpdated(rootNode.path("data"));
                        break;
                    case "user.deleted":
                        handleUserDeleted(rootNode.path("data"));
                        break;
                }
                return ResponseEntity.ok().build();
            }catch(Exception e){
                response = ByeByeBGResponse.builder()
                                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .data("Something went wrong")
                                                .success(false)
                                                        .build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(response);
            }
        }

    private void handleUserDeleted(JsonNode data) {
        String clerkId = data.path("id").asText();
        userService.deleteUserByClerkId(clerkId);
        }

    private void handleUserUpdated(JsonNode data) {
        String clerkId = data.path("id").asText();
        UserDto existingUser = userService.getUserByClerkId(clerkId);
        existingUser.setEmail(data.path("email_addresses").path(0).path("email_address").asText());
        existingUser.setFirstName(data.path("first_name").asText());
        existingUser.setLastName(data.path("last_name").asText());
        existingUser.setPhotoUrl(data.path("image_url").asText());
        userService.saveUser(existingUser);
        System.out.println(clerkId);
        System.out.println(data.path("last_name").asText());
        System.out.println(data.path("first_name").asText());
        System.out.println("Updated!!!");
        }

    private void handleUserCreated(JsonNode data) {
        UserDto newUser = UserDto.builder()
                .clerkId(data.path("id").asText())
                .email(data.path("email_addresses").path(0).path("email_address").asText())
                .firstName(data.path("first_name").asText())
                .lastName(data.path("last_name").asText())
                .build();
        userService.saveUser(newUser);
    }

    private boolean verifyWebhookSignature(String svixid, String svixTimestamp, String svixSignature, String payload) {
            return true;
    }

}
