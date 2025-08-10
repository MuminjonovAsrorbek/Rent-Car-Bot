package uz.dev.rentcarbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.dev.rentcarbot.enums.PageEnum;
import uz.dev.rentcarbot.enums.PaymetMethodEnum;
import uz.dev.rentcarbot.payload.CarDTO;
import uz.dev.rentcarbot.payload.OfficeDTO;
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

        if (pageableDTO.isHasPrevious()) {

            row.add(InlineKeyboardButton.builder()
                    .text("◀️ Oldingi")
                    .callbackData("car_page:" + (pageableDTO.getCurrentPage() - 1))
                    .build());

        }

        row.add(InlineKeyboardButton.builder()
                .text("❌")
                .callbackData("car_close")
                .build());

        if (pageableDTO.isHasNext()) {

            row.add(InlineKeyboardButton.builder()
                    .text("Keyingi ▶️")
                    .callbackData("car_page:" + (pageableDTO.getCurrentPage() + 1))
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

        InlineKeyboardButton thirdBtn = new InlineKeyboardButton();

        thirdBtn.setText("♥️ / \uD83D\uDC94");
        thirdBtn.setCallbackData("car-favorite:" + carId);

        keyboard.add(List.of(firstBtn, thirdBtn));

        InlineKeyboardButton fourthBtn = new InlineKeyboardButton();

        fourthBtn.setText("\uD83D\uDCC5 Bron qilish");
        fourthBtn.setCallbackData("car-booking:" + carId);

        keyboard.add(List.of(fourthBtn));

        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;

    }

    @Override
    public InlineKeyboardMarkup buildPages(Long id, PageableDTO pageableDTO, PageEnum pageEnum) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = getNextAndPrevBtns(id, pageableDTO, pageEnum);

        keyboard.add(row);

        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;

    }

    private static List<InlineKeyboardButton> getNextAndPrevBtns(Long id, PageableDTO pageableDTO, PageEnum pageEnum) {
        List<InlineKeyboardButton> row = new ArrayList<>();

        if (pageableDTO.isHasPrevious()) {

            row.add(InlineKeyboardButton.builder()
                    .text("◀️ Oldingi")
                    .callbackData("page:" + pageEnum + ":" + id + ":" + (pageableDTO.getCurrentPage() - 1))
                    .build());

        }

        row.add(InlineKeyboardButton.builder()
                .text("❌")
                .callbackData("close")
                .build());

        if (pageableDTO.isHasNext()) {

            row.add(InlineKeyboardButton.builder()
                    .text("Keyingi ▶️")
                    .callbackData("page:" + pageEnum + ":" + id + ":" + (pageableDTO.getCurrentPage() + 1))
                    .build());


        }
        return row;
    }

    @Override
    public InlineKeyboardMarkup buildIsForSelfOr() {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton firstBtn = new InlineKeyboardButton();

        firstBtn.setText("Oʻzim uchun");
        firstBtn.setCallbackData("for-me");

        keyboard.add(List.of(firstBtn));

        InlineKeyboardButton secondBtn = new InlineKeyboardButton();

        secondBtn.setText("Boshqa birov uchun");
        secondBtn.setCallbackData("for-other");

        keyboard.add(List.of(secondBtn));

        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;

    }

    @Override
    public InlineKeyboardMarkup buildPaymentMethod() {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        PaymetMethodEnum[] values = PaymetMethodEnum.values();

        for (int i = 0; i < values.length; i += 2) {

            List<InlineKeyboardButton> row = new ArrayList<>();

            row.add(InlineKeyboardButton.builder()
                    .text(values[i].toString())
                    .callbackData("payment:" + values[i].name())
                    .build());

            if (i + 1 < values.length) {

                row.add(InlineKeyboardButton.builder()
                        .text(values[i + 1].toString())
                        .callbackData("payment:" + values[i + 1].name())
                        .build());
            }
            keyboard.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;

    }

    @Override
    public InlineKeyboardMarkup buildOffices(PageableDTO pageableDTO) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<OfficeDTO> cars = (List<OfficeDTO>) pageableDTO.getObjects();

        for (int i = 0; i < cars.size(); i += 5) {

            List<InlineKeyboardButton> row = new ArrayList<>();

            for (int j = i; j < i + 5 && j < cars.size(); j++) {

                row.add(InlineKeyboardButton.builder()
                        .text("\uD83C\uDFE2" + (j + 1))
                        .callbackData("office:" + cars.get(j).getId())
                        .build());
            }
            keyboard.add(row);

        }

        List<InlineKeyboardButton> nextAndPrevBtns = getNextAndPrevBtns(0L, pageableDTO, PageEnum.OFFICE);

        keyboard.add(nextAndPrevBtns);

        inlineKeyboardMarkup.setKeyboard(keyboard);

        return  inlineKeyboardMarkup;
    }

}
