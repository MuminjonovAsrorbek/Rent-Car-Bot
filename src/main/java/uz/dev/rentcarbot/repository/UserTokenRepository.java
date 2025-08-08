package uz.dev.rentcarbot.repository;

import org.springframework.data.repository.CrudRepository;
import uz.dev.rentcarbot.model.UserToken;

public interface UserTokenRepository extends CrudRepository<UserToken, String> {
}
