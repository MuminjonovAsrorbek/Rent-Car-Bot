package uz.dev.rentcarbot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import uz.dev.rentcarbot.service.template.AdminTextService;

/**
 * Created by: asrorbek
 * DateTime: 8/13/25 20:17
 **/

@Service
public class AdminTextServiceImpl implements AdminTextService {
    @Override
    public BotApiMethod<?> process(Message message) {
        return null;
    }
}
