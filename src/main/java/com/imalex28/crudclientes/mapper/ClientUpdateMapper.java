
package com.imalex28.crudclientes.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.imalex28.crudclientes.dto.client.ClienteUpdateDTO;
import com.imalex28.crudclientes.model.Client;

@Mapper(componentModel = "cdi")
public interface ClientUpdateMapper {

    @Mapping(source = "id", target = "clientId")
    @Mapping(source = "nombre", target = "name")
    @Mapping(source = "apellidos", target = "surname")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "registerDate", target = "registerDate")
    Client toCliente(ClienteUpdateDTO dto);

}
