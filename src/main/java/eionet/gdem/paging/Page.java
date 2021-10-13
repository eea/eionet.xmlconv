package eionet.gdem.paging;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Page<T> {

    List<T> content;
    int totalPages;
    
}