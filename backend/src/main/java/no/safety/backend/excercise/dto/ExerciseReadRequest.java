package no.safety.backend.excercise.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

/**
 * DTO for requesting a paginated list of exercises.
 *
 * @param page the page number to retrieve (0-indexed, defaults to 0)
 * @param size the number of exercises per page (1-100, defaults to 20)
 */
public record ExerciseReadRequest(
    @PositiveOrZero
    Integer page,

    @Min(1)
    @Max(100)
    Integer size
) {

  /**
   * Compact constructor that applies default values when parameters are null.
   */
  public ExerciseReadRequest {
    page = (page == null) ? 0 : page;
    size = (size == null) ? 20 : size;
  }

  /**
   * Converts this request to a Spring Data Pageable object.
   * Results are sorted by publicId in descending order.
   *
   * @return a Pageable object for pagination
   */
  public Pageable toPageable() {
    Sort sort = Sort.by(Direction.DESC, "publicId");
    return PageRequest.of(page, size, sort);
  }

}

  public Pageable toPageable() {
    Sort sort = Sort.by(Direction.DESC, "publicId");
    return PageRequest.of(page, size, sort);
  }

}
