package no.safety.backend.notepad.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import org.springframework.data.domain.PageRequest;

/**
 * DTO for notepad entry read requests.
 * Contains pagination parameters for retrieving notepad entries.
 */
public record NotepadEntryReadRequest(
    @PositiveOrZero 
    Integer page,
    
    @Min(1) 
    @Max(100) 
    Integer size
) {
    /**
     * Compact constructor that sets default values for pagination parameters.
     * Page defaults to 0 and size defaults to 20 if not specified.
     */
    public NotepadEntryReadRequest {
        page = (page == null) ? 0 : page;
        size = (size == null) ? 20 : size;
    }

    /**
     * Converts this request to a Spring Data {@link Pageable} object.
     *
     * @return a Pageable instance with the specified page, size, and sort by date created descending
     */
    public Pageable toPageable() {
        Sort sort = Sort.by(Sort.Direction.DESC, "dateCreated");
        return PageRequest.of(page, size, sort);
    }
}
