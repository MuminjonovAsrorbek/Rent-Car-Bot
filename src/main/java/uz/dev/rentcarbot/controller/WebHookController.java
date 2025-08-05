package uz.dev.rentcarbot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.dev.rentcarbot.config.MyTelegramBot;

/**
 * Created by: asrorbek
 * DateTime: 8/5/25 15:32
 **/

@RestController
@RequiredArgsConstructor
public class WebHookController {

    private final MyTelegramBot telegramBot;

    @PostMapping("${telegram.bots.bots-list.bot.path}")
    public ResponseEntity<?> onUpdateReceived(@RequestBody Update update) {

        BotApiMethod<?> apiMethod = telegramBot.onWebhookUpdateReceived(update);

        if (apiMethod != null)
            return ResponseEntity.ok(apiMethod);

        return ResponseEntity.notFound().build();

    }

}
