package uz.dev.rentcarbot.service.template;

import org.springframework.stereotype.Service;
import uz.dev.rentcarbot.payload.CarDTO;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConversationStateService {

    private final Map<Long, CarDTO> carCreationState = new ConcurrentHashMap<>();

    public void startCarCreation(Long chatId) {
        carCreationState.put(chatId, new CarDTO());
    }

    public CarDTO getState(Long chatId) {
        return carCreationState.get(chatId);
    }

    public void updateState(Long chatId, CarDTO carDTO) {
        carCreationState.put(chatId, carDTO);
    }

    public void clearState(Long chatId) {
        carCreationState.remove(chatId);
    }
}