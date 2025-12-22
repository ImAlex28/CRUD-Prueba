package com.imalex28.crudclientes.repository;
import java.util.List;

import com.imalex28.crudclientes.model.Cliente;

public interface ClienteRepository {
    List<Cliente> findAll();
    Cliente findById(Long id);
    void save(Cliente cliente);
    void update(Cliente cliente);
    void delete(Long id);
    boolean existsById(Long idCliente);
}

