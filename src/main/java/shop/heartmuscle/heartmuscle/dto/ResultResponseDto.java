package shop.heartmuscle.heartmuscle.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultResponseDto {
    private String result;
    private String msg;

    public ResultResponseDto(String result, String msg) {
        this.result = result;
        this.msg = msg;
    }
}

