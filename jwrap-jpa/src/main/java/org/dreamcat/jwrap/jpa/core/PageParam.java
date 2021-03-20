package org.dreamcat.jwrap.jpa.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Create by tuke on 2020/2/24
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class PageParam implements Pageable, Serializable {

    private int page;
    private int size;

    public static PageRequest of(PageParam param) {
        return PageRequest.of(param.getPage(), param.getSize());
    }

    @JsonIgnore
    @Override
    public int getPageNumber() {
        return page;
    }

    @JsonIgnore
    @Override
    public int getPageSize() {
        return size;
    }

    @JsonIgnore
    @Override
    public long getOffset() {
        return (long) (page - 1) * size;
    }

    @JsonIgnore
    @Override
    public Sort getSort() {
        return Sort.unsorted();
    }

    @Override
    public Pageable next() {
        return new PageParam(page + 1, size);
    }

    @Override
    public Pageable previousOrFirst() {
        return page == 1 ? first() : new PageParam(page - 1, size);
    }

    @Override
    public Pageable first() {
        return new PageParam(1, size);
    }

    @Override
    public boolean hasPrevious() {
        return page > 1;
    }
}
