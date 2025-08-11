package uz.dev.rentcarbot.service.template;

import uz.dev.rentcarbot.payload.ReviewDTO;

public interface ReviewBotService {

    String listReviews(Long carId,int page, int size);

    String createReview(Long carId,int rating,String comment,Long userId);

    String deleteReview(Long id,Long userId);

}
