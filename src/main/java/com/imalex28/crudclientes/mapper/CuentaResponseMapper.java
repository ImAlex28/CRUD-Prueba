package com.imalex28.crudclientes.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.imalex28.crudclientes.dto.account.CuentaResponseDTO;
import com.imalex28.crudclientes.model.Cuenta;

@Mapper(componentModel = "cdi")
public interface CuentaResponseMapper {
    // Mapear Entidad a DTO
    @Mapping(source = "idCuenta", target = "idCuenta")
    @Mapping(source = "cliente", target = "cliente")
    @Mapping(source = "numeroCuenta", target = "numeroCuenta")
    @Mapping(source = "tipoCuenta", target = "tipoCuenta")
    @Mapping(source = "saldo", target = "saldo")
    CuentaResponseDTO toCuentaResponseDTO(Cuenta cuenta);
    List<CuentaResponseDTO> toCuentaResponseDTOList(List<Cuenta> cuentas);

}
