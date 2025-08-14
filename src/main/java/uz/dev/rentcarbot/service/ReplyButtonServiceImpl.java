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

        if (role.equals(RoleEnum.USER)) {

            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);

            List<KeyboardRow> keyboardRows = new ArrayList<>();

            KeyboardRow firstKeyboardRow = new KeyboardRow();

            firstKeyboardRow.add("\uD83D\uDD11 Ijaraga olish");
            firstKeyboardRow.add("⚖️ Jarimalar");

            keyboardRows.add(firstKeyboardRow);

            KeyboardRow thirdKeyboardRow = new KeyboardRow();

            thirdKeyboardRow.add("Mening buyurtmalarim");

            keyboardRows.add(thirdKeyboardRow);

            KeyboardRow secondKeyboardRow = new KeyboardRow();

            secondKeyboardRow.add("\uD83D\uDD14 Xabarnomalar");
            secondKeyboardRow.add("❤️ Sevimlilar");

            keyboardRows.add(secondKeyboardRow);

            replyKeyboardMarkup.setKeyboard(keyboardRows);

            return replyKeyboardMarkup;
        } else if (role.equals(RoleEnum.ADMIN)) {

            ReplyKeyboardMarkup replyKeyboardMarkup = getReplyKeyboardMarkup();

            return replyKeyboardMarkup;

        }

        return null;

    }

    private static ReplyKeyboardMarkup getReplyKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow firstKeyboardRow = new KeyboardRow();

        firstKeyboardRow.add("\uD83D\uDC64 Foydalanuvchilar");
        firstKeyboardRow.add("\uD83D\uDE97 Avtomobillar");

        keyboardRows.add(firstKeyboardRow);

        KeyboardRow secondKeyboardRow = new KeyboardRow();

        secondKeyboardRow.add("\uD83D\uDCE6 Buyurtmalar");
        secondKeyboardRow.add("⚠ Jarimalar");

        keyboardRows.add(secondKeyboardRow);

        KeyboardRow thirdKeyboardRow = new KeyboardRow();

        thirdKeyboardRow.add("🏢 Ofislar");
        thirdKeyboardRow.add("📂 Kategoriyalar");

        keyboardRows.add(thirdKeyboardRow);

        KeyboardRow fourthKeyboardRow = new KeyboardRow();

        fourthKeyboardRow.add("🎟️ Promo kodlar");
        fourthKeyboardRow.add("📢 E'lonlar");

        keyboardRows.add(fourthKeyboardRow);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    @Override
    public ReplyKeyboardMarkup buildCancelButton() {

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow firstKeyboardRow = new KeyboardRow();

        firstKeyboardRow.add("Orqaga");

        keyboardRows.add(firstKeyboardRow);

        replyKeyboardMarkup.setKeyboard(keyboardRows);

        return replyKeyboardMarkup;

    }
}