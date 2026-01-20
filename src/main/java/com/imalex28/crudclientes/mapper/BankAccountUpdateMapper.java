package com.imalex28.crudclientes.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import org.mapstruct.Named;

import com.imalex28.crudclientes.dto.account.CuentaUpdateDTO;
import com.imalex28.crudclientes.model.Client;
import com.imalex28.crudclientes.model.BankAccount;
import com.imalex28.crudclientes.service.ClientService;

@Mapper(componentModel = "cdi")
public interface BankAccountUpdateMapper {
	

    @Mapping(target = "bankAccountId", source = "idCuenta") 
    @Mapping(target = "client", source = "idCliente", qualifiedByName = "idToCliente")
    @Mapping(source = "numeroCuenta", target = "accountNumber")
    @Mapping(source = "tipoCuenta", target = "accountType")
    @Mapping(source = "saldo", target = "balance")
    BankAccount toCuenta(CuentaUpdateDTO dto, @Context ClientService clientService);

    @Named("idToCliente")
    default Client idToCliente(Long clientId, @Context ClientService clientService) {

        return clientService.findById(clientId);
    }
}
