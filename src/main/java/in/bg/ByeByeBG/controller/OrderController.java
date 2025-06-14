package in.bg.ByeByeBG.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayException;
import in.bg.ByeByeBG.dto.OrderDto;
import in.bg.ByeByeBG.response.ByeByeBGResponse;
import in.bg.ByeByeBG.service.OrderService;
import in.bg.ByeByeBG.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final RazorpayService razorpayService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestParam String planId, Authentication authentication)throws RazorpayException {
        Map<String,Object> responseMap = new HashMap<>();
        ByeByeBGResponse response = null;

        if(authentication.getName().isEmpty() || authentication.getName() == null){
            response = ByeByeBGResponse.builder()
                    .statusCode(HttpStatus.FORBIDDEN)
                    .success(false)
                    .data("No permission!")
                    .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try{
            Order order = orderService.createOrder(planId, authentication.getName());
            OrderDto responseDto = convertToDto(order);
            response = ByeByeBGResponse.builder()
                    .success(true)
                    .data(responseDto)
                    .statusCode(HttpStatus.CREATED)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response = ByeByeBGResponse.builder()
                    .success(false)
                    .data("Something went wrong!!")
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    private OrderDto convertToDto(Order order) {
        return OrderDto.builder()
                .id(order.get("id"))
                .entity(order.get("entity"))
                .amount(order.get("amount"))
                .currency(order.get("currency"))
                .status(order.get("status"))
                .created_at(order.get("created_at"))
                .receipt(order.get("receipt"))
                .build();
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOrder(@RequestBody Map<String, Object> request) throws RazorpayException{

        try {
           String razorpayOrderId = request.get("razorpay_order_id").toString();
           Map<String,Object> returnValue =  razorpayService.verifyPayment(razorpayOrderId);
           return ResponseEntity.ok(returnValue);
       }catch (RazorpayException e){
            Map<String,Object> errorResponse = new HashMap<>();
            errorResponse.put("success",false);
            errorResponse.put("message",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
       }
    }
}
