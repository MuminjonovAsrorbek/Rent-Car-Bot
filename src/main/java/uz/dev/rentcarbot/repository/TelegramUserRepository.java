package uz.dev.rentcarbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import uz.dev.rentcarbot.entity.TelegramUser;
import uz.dev.rentcarbot.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;

public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {


    Optional<TelegramUser> findByChatId(Long chatId);

    default TelegramUser findByChatIdOrThrowException(Long chatId) {

        return this.findByChatId(chatId).orElseThrow(() -> new UserNotFoundException("Sizning xisobingiz topilmadi", HttpStatus.NOT_FOUND, chatId));

    }

}