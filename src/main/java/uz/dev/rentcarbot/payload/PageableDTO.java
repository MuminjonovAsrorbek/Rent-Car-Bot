package uz.dev.rentcarbot.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by: asrorbek
 * DateTime: 6/22/25 10:58
 **/

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageableDTO<T> {

    private Integer size;

    private Integer currentPage;

    private Long totalElements;

    private Integer totalPages;

    private boolean hasNext;

    private boolean hasPrevious;

    private List<T> objects;

}
