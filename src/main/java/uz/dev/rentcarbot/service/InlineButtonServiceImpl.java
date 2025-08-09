package uz.dev.rentcarbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.dev.rentcarbot.payload.CarDTO;
import uz.dev.rentcarbot.payload.PageableDTO;
import uz.dev.rentcarbot.service.template.InlineButtonService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: asrorbek
 * DateTime: 8/8/25 18:21
 **/

@Service
@RequiredArgsConstructor
public class InlineButtonServiceImpl implements InlineButtonService {


    @Override
    public InlineKeyboardMarkup buildAvailableCars() {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton firstBtn = new InlineKeyboardButton();

        firstBtn.setText("\uD83D\uDCCB Mavjud mashinalar");
        firstBtn.setCallbackData("available-cars");

        rows.add(List.of(firstBtn));

        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;

    }

    @Override
    public InlineKeyboardMarkup getAvailableCars(PageableDTO pageableDTO) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<CarDTO> cars = (List<CarDTO>) pageableDTO.getObjects();

        for (int i = 0; i < cars.size(); i += 3) {

            List<InlineKeyboardButton> row = new ArrayList<>();

            for (int j = i; j < i + 3 && j < cars.size(); j++) {

                row.add(InlineKeyboardButton.builder()
                        .text("\uD83D\uDE97 " + (j + 1))
                        .callbackData("car:" + cars.get(j).getId())
                        .build());

            }

            keyboard.add(row);

        }

        List<InlineKeyboardButton> row = new ArrayList<>();

        if (pageableDTO.isHasNext()) {

            row.add(InlineKeyboardButton.builder()
                    .text("Keyingi ▶️")
                    .callbackData("car_page:" + (pageableDTO.getCurrentPage() + 1))
                    .build());


        }

        row.add(InlineKeyboardButton.builder()
                .text("❌")
                .callbackData("car_close")
                .build());

        if (pageableDTO.isHasPrevious()) {

            row.add(InlineKeyboardButton.builder()
                    .text("◀️ Oldingi")
                    .callbackData("car_page:" + (pageableDTO.getCurrentPage() - 1))
                    .build());

        }

        keyboard.add(row);

        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;

    }

    @Override
    public InlineKeyboardMarkup buildCarInfo(String carId) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton firstBtn = new InlineKeyboardButton();

        firstBtn.setText("\uD83D\uDCAC Izohlar");
        firstBtn.setCallbackData("car-comment:" + carId);

        InlineKeyboardButton secondBtn = new InlineKeyboardButton();

        secondBtn.setText("\uD83D\uDCF7 Boshqa rasmlar");
        secondBtn.setCallbackData("car-images:" + carId);

        keyboard.add(List.of(firstBtn, secondBtn));

        InlineKeyboardButton thirdBtn = new InlineKeyboardButton();

        thirdBtn.setText("⚙️ Qo‘shimcha opsiyalar");
        thirdBtn.setCallbackData("car-features:" + carId);

        keyboard.add(List.of(thirdBtn));

        InlineKeyboardButton fourthBtn = new InlineKeyboardButton();

        fourthBtn.setText("\uD83D\uDCC5 Bron qilish");
        fourthBtn.setCallbackData("car-booking:" + carId);

        keyboard.add(List.of(fourthBtn));

        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;

    }

}
