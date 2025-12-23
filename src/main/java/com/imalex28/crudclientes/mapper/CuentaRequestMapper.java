package com.imalex28.crudclientes.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.imalex28.crudclientes.dto.CuentaRequestDTO;
import com.imalex28.crudclientes.model.Cuenta;


@Mapper(componentModel = "cdi")
public interface CuentaRequestMapper {
    // Mapear DTO a Entidad
    @Mapping(source = "cliente", target = "cliente")
    @Mapping(source = "numeroCuenta", target = "numeroCuenta")
    @Mapping(source = "tipoCuenta", target = "tipoCuenta")
    @Mapping(source = "saldo", target = "saldo")
    @Mapping(target = "idCuenta", ignore = true) // la entidad tiene id autogenerado
    Cuenta toCuenta(CuentaRequestDTO dto);
}
