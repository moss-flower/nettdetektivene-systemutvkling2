package no.safety.backend.notepad.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import no.safety.backend.notepad.model.NotepadEntry;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.data.domain.PageRequest;

/**
 * Defines a {@link NotepadEntry} request for GET requests.
 */
public record NotepadEntryReadRequest(
    @PositiveOrZero 
    Integer page,
    
    @Min(1) 
    @Max(100) 
    Integer size
) {
    /**
     * NoArgs constructor to set default values.
     */
    public NotepadEntryReadRequest {
        page = (page == null) ? 0 : page;
        size = (size == null) ? 20 : size;
    }

    public Pageable toPageable() {
        Sort sort = Sort.by(Sort.Direction.DESC, "dateCreated");
        return PageRequest.of(page, size, sort);
    }
}
