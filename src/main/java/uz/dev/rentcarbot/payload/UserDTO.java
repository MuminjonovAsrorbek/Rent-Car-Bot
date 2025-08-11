package uz.dev.rentcarbot.payload;

import lombok.Data;
import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Set<String> roles;
}