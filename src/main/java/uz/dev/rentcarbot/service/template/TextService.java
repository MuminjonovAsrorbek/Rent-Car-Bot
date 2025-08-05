package uz.dev.rentcarbot.service.template;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Created by: asrorbek
 * DateTime: 8/5/25 16:34
 **/

public interface TextService {

    BotApiMethod<?> processText(Message message);

}
