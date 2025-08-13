package uz.dev.rentcarbot.service.template;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Created by: asrorbek
 * DateTime: 8/13/25 20:16
 **/

public interface AdminTextService {
    BotApiMethod<?> process(Message message);
}
