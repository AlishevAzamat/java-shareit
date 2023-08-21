package ru.practicum.shareit.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PaginationUtil {

    public static PageRequest getPageRequestDesc(int from, int size, String sortBy) {
        int pageNumber = (from + size - 1) / size;
        return PageRequest.of(pageNumber, size, Sort.by(sortBy).descending());
    }

    public static PageRequest getPageRequestAsc(int from, int size, String sortBy) {
        int pageNumber = (from + size - 1) / size;
        return PageRequest.of(pageNumber, size, Sort.by(sortBy).ascending());
    }
}
