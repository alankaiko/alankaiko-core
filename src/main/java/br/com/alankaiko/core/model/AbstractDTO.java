package br.com.alankaiko.core.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class AbstractDTO implements Pageable {
    private int pagina;

    private int itensPorPagina;

    @Override
    public int getPageNumber() {
        return this.getPagina();
    }

    @Override
    public int getPageSize() {
        return this.getItensPorPagina();
    }

    @Override
    public long getOffset() {
        return this.itensPorPagina * this.getPagina();
    }

    @Override
    public Sort getSort() {
        return Sort.by("codigo").ascending();
    }

    @Override
    public Pageable next() {
        return null;
    }

    @Override
    public Pageable previousOrFirst() {
        return null;
    }

    @Override
    public Pageable first() {
        return null;
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return null;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }
}
