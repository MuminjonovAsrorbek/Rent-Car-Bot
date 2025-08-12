package uz.dev.rentcarbot.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.dev.rentcarbot.enums.PenaltyStatusEnum;

import java.io.Serializable;
import java.sql.Timestamp;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PenaltyDTO implements Serializable {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Timestamp penaltyDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Timestamp updatedAt;

    private Long bookingId;

    private Long penaltyAmount;

    private Long overdueDays;

    private PenaltyStatusEnum status;
}