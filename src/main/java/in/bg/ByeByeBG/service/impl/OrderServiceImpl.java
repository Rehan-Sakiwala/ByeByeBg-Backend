package in.bg.ByeByeBG.service.impl;

import com.razorpay.Order;
import com.razorpay.RazorpayException;
import in.bg.ByeByeBG.entity.OrderEntity;
import in.bg.ByeByeBG.repository.OrderRepository;
import in.bg.ByeByeBG.service.OrderService;
import in.bg.ByeByeBG.service.RazorpayService;
import in.bg.ByeByeBG.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final RazorpayService razorpayService;
    private final UserService userService;
    private final OrderRepository orderRepository;

    private static final Map<String,PlanDetails>PLAN_DETAILS = Map.of(
            "Basic",new PlanDetails("Basic",20,199.00),
            "Gold",new PlanDetails("Gold",45,399.00),
            "Ultra",new PlanDetails("Ultra",75,699.00)
    );

    private record PlanDetails(String name, int credits, double amount){

    }

    @Override
    public Order createOrder(String planId, String clerkId) throws RazorpayException {
        PlanDetails details = PLAN_DETAILS.get(planId);
        if(details==null){
            throw new IllegalArgumentException("Invalid Planid: "+planId);
        }
        try {
            Order razorPayOrder = razorpayService.createOrder(details.amount(),"INR");
            OrderEntity newOrder = OrderEntity.builder()
                    .clerkId(clerkId)
                    .plan(details.name())
                    .credits(details.credits())
                    .amount(details.amount())
                    .orderId(razorPayOrder.get("id"))
                    .build();
            orderRepository.save(newOrder);
            return razorPayOrder;
        } catch (Exception e) {
            throw new RazorpayException("Error while placing order! ",e);
        }
    }
}
