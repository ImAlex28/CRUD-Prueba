
package com.imalex28.crudclientes.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.imalex28.crudclientes.dto.account.CuentaRequestDTO;
import com.imalex28.crudclientes.model.Client;
import com.imalex28.crudclientes.model.BankAccount;
import com.imalex28.crudclientes.service.ClientService;

@Mapper(componentModel = "cdi") 
public interface BankAccountRequestMapper {
    // Mapear DTO a Entidad
    @Mapping(target = "client", source = "idCliente", qualifiedByName = "idToCliente")
    @Mapping(source = "numeroCuenta", target = "accountNumber")
    @Mapping(source = "tipoCuenta", target = "accountType")
    @Mapping(source = "saldo", target = "balance")
    @Mapping(target = "bankAccountId", ignore = true) // la entidad tiene id autogenerado
    BankAccount toCuenta(CuentaRequestDTO dto, @Context ClientService clientService);
    

    @Named("idToCliente")
    default Client idToCliente(Long clientId, @Context ClientService clientService) {
        return clientService.findById(clientId);
    }


}
