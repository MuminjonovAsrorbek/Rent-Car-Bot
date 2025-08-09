package uz.dev.rentcarbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Attachment {

    private Long id;

    private String path;

    private String originalName;

    private String contentType;

    private Long size;

    private boolean isPrimary = false;

    private Long carId;
}