package uz.dev.rentcarbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.dev.rentcarbot.service.template.MessageService;
import uz.dev.rentcarbot.service.template.UpdateDispatcherService;

/**
 * Created by: asrorbek
 * DateTime: 8/5/25 15:51
 **/

@Service
@RequiredArgsConstructor
public class UpdateDispatcherServiceImpl implements UpdateDispatcherService {

    private final MessageService messageService;


    @Override
    public BotApiMethod<?> updateDispatch(Update update) {

        if (update.hasMessage()) {

            return messageService.processMessage(update.getMessage());

        }

        return null;

    }
}
