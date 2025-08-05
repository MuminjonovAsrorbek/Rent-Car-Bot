package uz.dev.rentcarbot.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import uz.dev.rentcarbot.enums.RoleEnum;
import uz.dev.rentcarbot.enums.StepEnum;

/**
 * Created by: asrorbek
 * DateTime: 8/5/25 15:52
 **/

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "telegram_users")
@FieldNameConstants
public class TelegramUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long chatId;

    @Column(nullable = false)
    private String firstName;

    private String username;

    @Enumerated(EnumType.STRING)
    private StepEnum step;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    private String phoneNumber;

    private Long userId;

}
