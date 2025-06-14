package in.bg.ByeByeBG.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {
    private String id;
    private String entity;
    private String currency;
    private String status;
    private Integer amount;
    private Date created_at;
    private String receipt;
}
