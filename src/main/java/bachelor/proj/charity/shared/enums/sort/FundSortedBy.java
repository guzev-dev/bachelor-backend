package bachelor.proj.charity.shared.enums.sort;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import org.springframework.data.domain.Sort;

import java.util.Arrays;

@Getter
public enum FundSortedBy {
    CREATED_DESC("Нові", Sort.by("id").descending()),
    ALPHABET_ASC("За алфавітом: від А до Я", Sort.by("name").ascending()),
    ALPHABET_DESC("За алфавітом: від Я до А", Sort.by("name").descending());

    private final String name;
    private final Sort sort;

    FundSortedBy(String name, Sort sort) {
        this.name = name;
        this.sort = sort;
    }

    public static FundSortedBy getByName(String name) {
        return Arrays.stream(FundSortedBy.values())
                .filter(arg -> arg.name.equals(name))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Невірне ім'я порядку сортування фондів."));
    }
}
