package com.imalex28.crudclientes.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.imalex28.crudclientes.dto.client.ClienteResponseDTO;
import com.imalex28.crudclientes.model.Cliente;

@Mapper(componentModel = "cdi")
public interface ClienteResponseMapper {
    // Mapear Entidad a DTO
    @Mapping(source = "nombre", target = "nombre")
    @Mapping(source = "apellidos", target = "apellidos")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "idCliente", target = "id")
    ClienteResponseDTO toClienteResponseDTO(Cliente cliente);
    List<ClienteResponseDTO> toClienteResponseDTOList(List<Cliente> clientes);

}
