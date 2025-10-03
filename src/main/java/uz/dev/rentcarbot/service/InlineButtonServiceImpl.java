package uz.dev.rentcarbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.dev.rentcarbot.enums.BookingStatusEnum;
import uz.dev.rentcarbot.enums.PageEnum;
import uz.dev.rentcarbot.enums.PaymetMethodEnum;
import uz.dev.rentcarbot.payload.*;
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

    @Override
    public InlineKeyboardMarkup buildPagesForMyBookings(PageableDTO pageableDTO, PageEnum pageEnum) {

        List<BookingDTO> bookingDTOS = pageableDTO.getObjects();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        if (bookingDTOS.get(0).getStatus().equals(BookingStatusEnum.COMPLETED)) {

            InlineKeyboardButton btn = new InlineKeyboardButton();

            btn.setText("⭐ Reyting berish");
            btn.setCallbackData("review:" + bookingDTOS.get(0).getCarId());

            keyboard.add(List.of(btn));

        }

        List<InlineKeyboardButton> row = getNextAndPrevBtns(0L, pageableDTO, pageEnum);

        keyboard.add(row);

        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;

    }

    private List<InlineKeyboardButton> getNextAndPrevBtns(Long id, PageableDTO pageableDTO, PageEnum pageEnum) {
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

        return inlineKeyboardMarkup;
    }

    @Override
    public InlineKeyboardMarkup buildFavorites(PageableDTO pageableDTO) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<FavoriteDTO> favoriteDTOS = (List<FavoriteDTO>) pageableDTO.getObjects();

        for (int i = 0; i < favoriteDTOS.size(); i += 5) {

            List<InlineKeyboardButton> row = new ArrayList<>();

            for (int j = i; j < i + 5 && j < favoriteDTOS.size(); j++) {

                row.add(InlineKeyboardButton.builder()
                        .text(String.valueOf((j + 1)))
                        .callbackData("favorite:" + favoriteDTOS.get(j).getCarId())
                        .build());
            }
            keyboard.add(row);

        }

        List<InlineKeyboardButton> nextAndPrevBtns = getNextAndPrevBtns(0L, pageableDTO, PageEnum.FAVORITE);

        keyboard.add(nextAndPrevBtns);

        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;

    }

    @Override
    public InlineKeyboardMarkup buildYesOrNo(String prefix) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton firstBtn = new InlineKeyboardButton();

        firstBtn.setText("Ha");
        firstBtn.setCallbackData(prefix + ":yes");

        InlineKeyboardButton secondBtn = new InlineKeyboardButton();

        secondBtn.setCallbackData(prefix + ":no");
        secondBtn.setText("Yo'q");

        keyboard.add(List.of(firstBtn, secondBtn));

        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;
    }

    @Override
    public InlineKeyboardMarkup buildPenaltyMenu() {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton firstBtn = new InlineKeyboardButton();

        firstBtn.setText("\uD83C\uDD95 Yangi jarimalar");
        firstBtn.setCallbackData("penalty-new");

        keyboard.add(List.of(firstBtn));

        InlineKeyboardButton secondBtn = new InlineKeyboardButton();

        secondBtn.setText("\uD83D\uDCDC Barcha jarimalar");
        secondBtn.setCallbackData("penalty-all");

        keyboard.add(List.of(secondBtn));

        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;

    }

    @Override
    public InlineKeyboardMarkup buildNotificationPages(PageableDTO pageableDTO, PageEnum pageEnum) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton firstBtn = new InlineKeyboardButton();

        firstBtn.setText("✅ Hammasini o‘qildi");
        firstBtn.setCallbackData("notification-read");

        InlineKeyboardButton secondBtn = new InlineKeyboardButton();

        secondBtn.setText("\uD83D\uDCE9 O‘qilmaganlar");
        secondBtn.setCallbackData("notification-unread");

        keyboard.add(List.of(firstBtn, secondBtn));

        List<InlineKeyboardButton> row = getNextAndPrevBtns(0L, pageableDTO, pageEnum);

        keyboard.add(row);

        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;

    }

    @Override
    public InlineKeyboardMarkup buildBookingMenu() {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton firstBtn = new InlineKeyboardButton();

        firstBtn.setText("✅ Tasdiqlash");
        firstBtn.setCallbackData("booking-confirm");

        InlineKeyboardButton secondBtn = new InlineKeyboardButton();

        secondBtn.setText("\uD83D\uDCE5 Qabul qilish");
        secondBtn.setCallbackData("booking-complete");

        keyboard.add(List.of(firstBtn, secondBtn));

        InlineKeyboardButton thirdBtn = new InlineKeyboardButton();

        thirdBtn.setText("❌ Bekor qilish");
        thirdBtn.setCallbackData("booking-cancel");

        keyboard.add(List.of(thirdBtn));

        InlineKeyboardButton fourthBtn = new InlineKeyboardButton();

        fourthBtn.setText("\uD83D\uDCB3 To‘lov tasdiq");
        fourthBtn.setCallbackData("payment-confirm");

        InlineKeyboardButton fiveBtn = new InlineKeyboardButton();

        fiveBtn.setText("\uD83D\uDEAB To‘lov bekor");
        fiveBtn.setCallbackData("payment-cancel");

        keyboard.add(List.of(fourthBtn, fiveBtn));

        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;

    }

    @Override
    public InlineKeyboardMarkup buildPenaltyMenuForAdmin() {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton firstBtn = new InlineKeyboardButton();

        firstBtn.setText("✅ Tasdiqlash (Booking ID)");
        firstBtn.setCallbackData("penalty-booking-confirm");

        InlineKeyboardButton secondBtn = new InlineKeyboardButton();

        secondBtn.setText("❌ Bekor qilish (Booking ID)");
        secondBtn.setCallbackData("penalty-booking-cancel");

        keyboard.add(List.of(firstBtn, secondBtn));

        InlineKeyboardButton thirdBtn = new InlineKeyboardButton();

        thirdBtn.setText("✅ Tasdiqlash (Penalty ID)");
        thirdBtn.setCallbackData("penalty-confirm");

        InlineKeyboardButton fourthBtn = new InlineKeyboardButton();

        fourthBtn.setText("❌ Bekor qilish (Penalty ID)");
        fourthBtn.setCallbackData("penalty-cancel");

        keyboard.add(List.of(thirdBtn, fourthBtn));

        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;
    }

    @Override
    public InlineKeyboardMarkup buildNotificationMSG() {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton sendAllBtn = new InlineKeyboardButton();
        sendAllBtn.setText("📢 Hammaga yuborish");
        sendAllBtn.setCallbackData("ANNOUNCE_ALL");


        InlineKeyboardButton sendOneBtn = new InlineKeyboardButton();
        sendOneBtn.setText("👤 Bitta foydalanuvchiga");
        sendOneBtn.setCallbackData("ANNOUNCE_ONE");

        rows.add(List.of(sendAllBtn));
        rows.add(List.of(sendOneBtn));

        markup.setKeyboard(rows);

        return markup;
    }

    @Override
    public InlineKeyboardMarkup buildRating(Long carId) {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton firstBtn = new InlineKeyboardButton();

        firstBtn.setText("⭐️");
        firstBtn.setCallbackData("rating:1:" + carId);

        rows.add(List.of(firstBtn));

        InlineKeyboardButton secondBtn = new InlineKeyboardButton();

        secondBtn.setText("⭐️ ⭐️");
        secondBtn.setCallbackData("rating:2:" + carId);

        rows.add(List.of(secondBtn));

        InlineKeyboardButton thirdBtn = new InlineKeyboardButton();

        thirdBtn.setText("⭐️ ⭐️ ⭐️️️");
        thirdBtn.setCallbackData("rating:3:" + carId);

        rows.add(List.of(thirdBtn));

        InlineKeyboardButton fourthBtn = new InlineKeyboardButton();

        fourthBtn.setText("⭐️ ⭐️ ⭐️ ⭐️️️");
        fourthBtn.setCallbackData("rating:4:" + carId);

        rows.add(List.of(fourthBtn));

        InlineKeyboardButton fiveBtn = new InlineKeyboardButton();

        fiveBtn.setText("⭐️ ⭐️ ⭐️ ⭐️ ⭐️️️");
        fiveBtn.setCallbackData("rating:5:" + carId);

        rows.add(List.of(fiveBtn));

        markup.setKeyboard(rows);

        return markup;

    }

}
