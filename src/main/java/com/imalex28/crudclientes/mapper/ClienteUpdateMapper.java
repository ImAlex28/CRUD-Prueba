
package com.imalex28.crudclientes.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.imalex28.crudclientes.dto.client.ClienteUpdateDTO;
import com.imalex28.crudclientes.model.Cliente;

@Mapper(componentModel = "cdi")
public interface ClienteUpdateMapper {

    @Mapping(source = "id", target = "idCliente")
    @Mapping(source = "nombre", target = "nombre")
    @Mapping(source = "apellidos", target = "apellidos")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "registerDate", target = "registerDate")
    Cliente toCliente(ClienteUpdateDTO dto);

}
