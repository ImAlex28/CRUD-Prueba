package com.imalex28.crudclientes.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.imalex28.crudclientes.dto.client.ClienteResponseDTO;
import com.imalex28.crudclientes.model.Client;

@Mapper(componentModel = "cdi")
public interface ClientResponseMapper {
    // Mapear Entidad a DTO
    @Mapping(source = "name", target = "nombre")
    @Mapping(source = "surname", target = "apellidos")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "clientId", target = "id")
    ClienteResponseDTO toClienteResponseDTO(Client cliente);
    List<ClienteResponseDTO> toClienteResponseDTOList(List<Client> clientes);

}
