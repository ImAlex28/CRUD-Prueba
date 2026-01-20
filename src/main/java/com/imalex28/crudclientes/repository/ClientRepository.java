package com.imalex28.crudclientes.repository;
import java.util.Date;
import java.util.List;

import com.imalex28.crudclientes.model.Client;

public interface ClientRepository {
    List<Client> findAll();
    Client findById(Long id);
    void save(Client cliente);
    void update(Client cliente);
    void delete(Long id);
    boolean existsById(Long idCliente);
    Client findByEmail(String email);
	int deleteOlderThan(Date cutoff);
}

