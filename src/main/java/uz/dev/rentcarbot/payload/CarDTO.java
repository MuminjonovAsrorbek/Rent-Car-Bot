package uz.dev.rentcarbot.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.dev.rentcarbot.enums.FuelTypeEnum;
import uz.dev.rentcarbot.enums.TransmissionEnum;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarDTO implements Serializable {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @NotNull
    private int year;

    @NotNull
    private Long pricePerDay;

    private boolean available = true;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String imageUrl;

    @NotNull
    private int seats;

    @NotNull
    private FuelTypeEnum fuelType;

    private BigDecimal fuelConsumption;

    @NotNull
    private TransmissionEnum transmission;

//    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
//    private List<CategoryDTO> categories;
//
//    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
//    private List<CarFeatureDTO> features;
//
//    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
//    private List<BookingDTO> bookings;
//
//    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
//    private List<ReviewDTO> reviews;
//
//    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
//    private List<AttachmentDTO> images;
//
//    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
//    private List<FavoriteDTO> favorites;
}