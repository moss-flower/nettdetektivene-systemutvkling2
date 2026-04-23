package no.safety.backend.excercise.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

public record ExerciseReadRequest(
    @PositiveOrZero
    Integer page,

    @Min(1)
    @Max(100)
    Integer size
) {

  public ExerciseReadRequest {
    page = (page == null) ? 0 : page;
    size = (size == null) ? 20: size;
  }

  public Pageable toPageable() {
    Sort sort = Sort.by(Direction.DESC, "publicId");
    return PageRequest.of(page, size, sort);
  }

}
