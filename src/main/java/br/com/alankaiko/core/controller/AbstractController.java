package br.com.alankaiko.core.controller;

import br.com.alankaiko.core.model.AbstractDTO;
import br.com.alankaiko.core.model.AbstractEntity;
import br.com.alankaiko.core.service.AbstractService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

public abstract class AbstractController<T extends AbstractEntity, D extends AbstractDTO> {
    private final AbstractService<T, D> service;

    public AbstractController(AbstractService<T, D> service) {
        this.service = service;
    }

    @PostMapping
    public T salvar(@Valid @RequestBody T entidade) {
        return this.service.salvar(entidade);
    }

    @DeleteMapping
    public void deletar(@PathVariable Long codigo) {
        this.service.deletar(codigo);
    }

    @GetMapping({"{codigo}"})
    public T buscarId(@PathVariable Long codigo) {
        return this.service.buscarId(codigo);
    }

    @GetMapping
    public List<T> listar() {
        return this.service.listar();
    }

    @PostMapping("listarPaginado")
    public Page<T> filtering(@RequestBody D filter) {
        return this.service.filtering(filter);
    }

    @GetMapping("imprimir/{codigo}/{enumImpressao}")
    public ResponseEntity<?> imprimir(@PathVariable Long codigo) {
        return this.service.imprimir(codigo);
    }
}
