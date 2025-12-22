package com.imalex28.crudclientes.repository;

import java.util.List;
import com.imalex28.crudclientes.model.Cuenta;

public interface CuentaRepository {
    List<Cuenta> findAll();
    Cuenta findById(Long id);
 
    // Recibe Long idCliente (el ID del cliente)
    // Internamente JPA hará la búsqueda por cliente.idCliente
    List<Cuenta> findByIdCliente(Long idCliente);
    
    Double getSaldoTotalByCliente(Long idCliente);    
    void save(Cuenta cuenta);
    void update(Cuenta cuenta);
    void delete(Long id);
    boolean existsById(Long idCuenta);

}