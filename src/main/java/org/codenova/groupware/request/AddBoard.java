package org.codenova.groupware.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddBoard {
    @NotNull
    private String writerId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
