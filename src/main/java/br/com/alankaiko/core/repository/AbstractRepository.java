package br.com.alankaiko.core.repository;

import br.com.alankaiko.core.model.AbstractDTO;
import br.com.alankaiko.core.model.AbstractEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AbstractRepository<T extends AbstractEntity, D extends AbstractDTO, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    Page findAll(Specification spec, Pageable pageable);

    <U> Page<U> findAll(Specification<U> spec, Pageable pageable, Class<U> returnType);

    Page<T> filtering(D filter);

}
