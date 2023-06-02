package bachelor.proj.charity.shared.enums.sort;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import org.springframework.data.domain.Sort;

import java.util.Arrays;

@Getter
public enum CharitySortedBy {
    UPVOTES("За голосами", Sort.by("upvotes").descending()),
    NAME_ALPHABET_ASC("За назвою: від А до Я", Sort.by("name").ascending()),
    NAME_ALPHABET_DESC("За назвою: від Я до А", Sort.by("name").descending()),
    END_DATE_ASC("За датою завершення: раніше", Sort.by("endDate").ascending()),
    END_DATE_DESC("За датою завершення: пізніше", Sort.by("endDate").descending()),
    COLLECTED_ASC("За кількістю зібраних коштів: спочатку менше", Sort.by("collected").ascending()),
    COLLECTED_DESC("За кількістю зібраних коштів: спочатку більше", Sort.by("collected").descending());

    private final String name;
    private final Sort sort;

    CharitySortedBy(String name, Sort sort) {
        this.name = name;
        this.sort = sort;
    }

    public static CharitySortedBy getByName(String name) {
        return Arrays.stream(CharitySortedBy.values())
                .filter(arg -> arg.name.equals(name))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Невірне ім'я порядку сортування благодійних зборів."));
    }
}