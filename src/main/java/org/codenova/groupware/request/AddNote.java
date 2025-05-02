package org.codenova.groupware.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AddNote {
    @NotBlank
    private String content;

    @NotEmpty
    private List<String> receiverIds;
}
