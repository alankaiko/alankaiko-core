package br.com.alankaiko.core.service.impl;

import br.com.alankaiko.core.model.AbstractDTO;
import br.com.alankaiko.core.model.AbstractEntity;
import br.com.alankaiko.core.repository.AbstractRepository;
import br.com.alankaiko.core.service.AbstractService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public abstract class AbstractServiceImpl<T extends AbstractEntity, D extends AbstractDTO> implements AbstractService<T, D> {
    protected AbstractRepository<T, D, Long> dao;

    public AbstractServiceImpl(AbstractRepository<T, D, Long> dao) {
        this.dao = dao;
    }

    @Override
    public T salvar(T entity) {
        return this.dao.save(entity);
    }

    @Override
    public void deletar(Long codigo) {
        this.dao.deleteById(codigo);
    }

    @Override
    public T buscarId(Long codigo) {
        return this.dao.findById(codigo).orElse((T) null);
    }

    @Override
    public List<T> listar() {
        return this.dao.findAll();
    }

    @Override
    public Page<T> filtering(D filter) {
        return this.dao.filtering(filter);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> imprimir(Long codigo) {
        return null;
    }
}
