package uz.dev.rentcarbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.dev.rentcarbot.client.ReviewClient;
import uz.dev.rentcarbot.payload.PageableDTO;
import uz.dev.rentcarbot.payload.ReviewDTO;
import uz.dev.rentcarbot.service.template.ReviewBotService;

@Service
@RequiredArgsConstructor
public class ReviewBotServiceImpl implements ReviewBotService {

    private final ReviewClient reviewClient;

    @Override
    public String listReviews(Long carId, int page, int size) {

        try {
            PageableDTO<ReviewDTO> pageableDTO = reviewClient.getReviewsByCarId(carId, page, size);

            if (pageableDTO.getObjects().isEmpty())
                return "ℹ\uFE0F Bu mashinaga review yo‘q";

            StringBuilder sb = new StringBuilder("📋 Reviewlar:\n");
            for (ReviewDTO reviewDTO : pageableDTO.getObjects()) {
                sb.append("⭐ ").append(reviewDTO.getComment())
                        .append(" - ").append(reviewDTO.getRating())
                        .append(" (").append(reviewDTO.getUserFullName()).append(")\n");

            }
            return sb.toString();

        } catch (Exception e) {
            return "❌ Reviewlarni olishda xatolik:" + e.getMessage();
        }

    }

    @Override
    public String createReview(Long carId, int rating, String comment, Long userId) {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setComment(comment);
        reviewDTO.setRating(rating);
        reviewDTO.setCarId(carId);
        reviewDTO.setUserId(userId);

        try {
            ReviewDTO saved = reviewClient.createReview(reviewDTO);
            return "✅ Review qo‘shildi! ID: " + saved.getId();
        } catch (Exception e) {
            return "❌ Xatolik: " + e.getMessage();
        }

    }

    @Override
    public String deleteReview(Long id, Long userId) {
        try {
            reviewClient.deleteReview(id, userId);
            return "\uD83D\uDDD1 Review deleted !";
        } catch (Exception e) {
            return "❌ Xatolik: " + e.getMessage();
        }
    }

}
