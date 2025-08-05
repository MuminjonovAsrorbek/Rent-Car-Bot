package uz.dev.rentcarbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.dev.rentcarbot.enums.RoleEnum;
import uz.dev.rentcarbot.service.template.ReplyButtonService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: asrorbek
 * DateTime: 8/5/25 16:49
 **/

@Service
@RequiredArgsConstructor
public class ReplyButtonServiceImpl implements ReplyButtonService {


    @Override
    public ReplyKeyboardMarkup buildPhoneNumber() {

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow firstKeyboardRow = new KeyboardRow();

        KeyboardButton phoneNumberButton = new KeyboardButton("\uD83D\uDCF2 Telefon raqamni yuborish");
        phoneNumberButton.setRequestContact(true);

        firstKeyboardRow.add(phoneNumberButton);

        keyboardRows.add(firstKeyboardRow);

        replyKeyboardMarkup.setKeyboard(keyboardRows);

        return replyKeyboardMarkup;
    }

    @Override
    public ReplyKeyboardMarkup buildMenuButtons(RoleEnum role) {
        return null;
    }
}
