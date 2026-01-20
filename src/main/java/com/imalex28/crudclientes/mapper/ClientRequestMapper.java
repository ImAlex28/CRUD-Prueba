package com.imalex28.crudclientes.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.imalex28.crudclientes.dto.client.ClienteRequestDTO;
import com.imalex28.crudclientes.model.Client;


@Mapper(componentModel = "cdi")
public interface ClientRequestMapper {
    // Mapear DTO a Entidad
    @Mapping(source = "nombre", target = "name")
    @Mapping(source = "apellidos", target = "surname")
    @Mapping(source = "email", target = "email")
    @Mapping(target = "clientId", ignore = true) // la entidad tiene id autogenerado
    @Mapping(source = "registerDate", target = "registerDate")
    Client toCliente(ClienteRequestDTO dto);
}
