package com.imalex28.crudclientes.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.imalex28.crudclientes.dto.account.CuentaResponseDTO;
import com.imalex28.crudclientes.model.BankAccount;

@Mapper(componentModel = "cdi")
public interface BankAccountResponseMapper {
    // Mapear Entidad a DTO
    @Mapping(source = "bankAccountId", target = "idCuenta")
    @Mapping(source = "client", target = "cliente")
    @Mapping(source = "accountNumber", target = "numeroCuenta")
    @Mapping(source = "accountType", target = "tipoCuenta")
    @Mapping(source = "balance", target = "saldo")
    CuentaResponseDTO toCuentaResponseDTO(BankAccount account);
    List<CuentaResponseDTO> toCuentaResponseDTOList(List<BankAccount> accounts);

}
