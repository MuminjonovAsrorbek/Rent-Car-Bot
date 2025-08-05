package uz.dev.rentcarbot.service.template;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Created by: asrorbek
 * DateTime: 8/5/25 15:51
 **/

public interface UpdateDispatcherService {

    BotApiMethod<?> updateDispatch(Update update);

}
