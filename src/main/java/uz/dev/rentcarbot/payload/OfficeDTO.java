package uz.dev.rentcarbot.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfficeDTO implements Serializable {

    private Long id;

    private String name;

    private String address;

    private BigDecimal latitude;

    private BigDecimal longitude;
}