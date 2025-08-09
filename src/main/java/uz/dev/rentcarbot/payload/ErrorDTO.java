package uz.dev.rentcarbot.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDTO {

    private int code;

    private String message;

    private List<FieldErrorDTO> fieldErrors;

    public ErrorDTO(int status, String message) {
        this.code = status;
        this.message = message;
    }
}
