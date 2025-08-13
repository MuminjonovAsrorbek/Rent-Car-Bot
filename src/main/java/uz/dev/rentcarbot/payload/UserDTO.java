package uz.dev.rentcarbot.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.dev.rentcarbot.enums.RoleEnum;

import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO implements Serializable {

    private Long id;

    private String fullName;

    private String email;

    private String phoneNumber;

    private RoleEnum role;

    private List<BookingDTO> bookings;

    private List<ReviewDTO> reviews;

    private List<FavoriteDTO> favorites;

    private List<NotificationDTO> notifications;
}