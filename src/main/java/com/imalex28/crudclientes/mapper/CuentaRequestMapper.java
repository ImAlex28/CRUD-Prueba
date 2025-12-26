
package com.imalex28.crudclientes.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.imalex28.crudclientes.dto.CuentaRequestDTO;
import com.imalex28.crudclientes.model.Cliente;
import com.imalex28.crudclientes.model.Cuenta;
import com.imalex28.crudclientes.service.ClienteService;

@Mapper(componentModel = "cdi") 
public interface CuentaRequestMapper {
    // Mapear DTO a Entidad
    @Mapping(target = "idCliente", source = "idCliente", qualifiedByName = "idToCliente")
    @Mapping(source = "numeroCuenta", target = "numeroCuenta")
    @Mapping(source = "tipoCuenta", target = "tipoCuenta")
    @Mapping(source = "saldo", target = "saldo")
    @Mapping(target = "idCuenta", ignore = true) // la entidad tiene id autogenerado
    Cuenta toCuenta(CuentaRequestDTO dto, @Context ClienteService clienteService);
    

    @Named("idToCliente")
    default Cliente idToCliente(Long idCliente, @Context ClienteService clienteService) {
        return clienteService.findById(idCliente);
    }


}
