package ru.practicum.model;

import lombok.*;
import java.util.List;

@Data
@RequiredArgsConstructor(onConstructor_=@NonNull)
public class Order {

    @NonNull
    private final List<String> ingredients;
}
