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

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow firstKeyboardRow = new KeyboardRow();

        firstKeyboardRow.add("\uD83D\uDD11 Ijaraga olish");
        firstKeyboardRow.add("⚖️ Jarimalar");

        keyboardRows.add(firstKeyboardRow);

        KeyboardRow secondKeyboardRow = new KeyboardRow();

        secondKeyboardRow.add("\uD83D\uDD14 Xabarnomalar");
        secondKeyboardRow.add("❤️ Sevimlilar");

        keyboardRows.add(secondKeyboardRow);

        replyKeyboardMarkup.setKeyboard(keyboardRows);

        return replyKeyboardMarkup;

    }

    @Override
    public ReplyKeyboardMarkup buildAdminMenuButtons() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Mashinalarni boshqarish");
        row1.add("Foydalanuvchilarni ko'rish");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Bronlarni boshqarish");
        row2.add("Promo-kodlar");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("⬅️ Oddiy menyuga qaytish");

        keyboardRows.add(row1);
        keyboardRows.add(row2);
        keyboardRows.add(row3);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }
}