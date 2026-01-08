package com.imalex28.crudclientes.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.imalex28.crudclientes.dto.client.ClienteRequestDTO;
import com.imalex28.crudclientes.model.Cliente;


@Mapper(componentModel = "cdi")
public interface ClienteRequestMapper {
    // Mapear DTO a Entidad
    @Mapping(source = "nombre", target = "nombre")
    @Mapping(source = "apellidos", target = "apellidos")
    @Mapping(source = "email", target = "email")
    @Mapping(target = "idCliente", ignore = true) // la entidad tiene id autogenerado
    Cliente toCliente(ClienteRequestDTO dto);
}
