
package com.imalex28.crudclientes.unit.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.imalex28.crudclientes.dto.account.CuentaUpdateDTO;
import com.imalex28.crudclientes.mapper.BankAccountUpdateMapper;
import com.imalex28.crudclientes.model.Client;
import com.imalex28.crudclientes.model.BankAccount;
import com.imalex28.crudclientes.service.ClientService;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CuentaUpdateMapperTest {

  // Instanciamos la implementación generada por MapStruct
  private final BankAccountUpdateMapper mapper = Mappers.getMapper(BankAccountUpdateMapper.class);

  @Mock
  ClientService clienteService;

  @Test
  void toCuenta_mapeaTodosLosCampos_cuandoClienteExiste() {
    // Arrange: DTO con todos los campos
    CuentaUpdateDTO dto = new CuentaUpdateDTO();
    dto.setIdCuenta(1001L);
    dto.setIdCliente(2002L);
    dto.setNumeroCuenta("ES12 3456 7890 1234 5678 9012");
    dto.setTipoCuenta("AHORRO"); // si es Enum, ajusta el tipo
    dto.setSaldo(1234.56d);      // double según tu entity

    // Mock: el servicio devuelve un Cliente para ese id
    Client cliente = new Client();
    cliente.setClientId(2002L);
    cliente.setName("Alejandro");
    when(clienteService.findById(2002L)).thenReturn(cliente);

    // Act
    BankAccount entity = mapper.toCuenta(dto, clienteService);

    // Assert: mapeo correcto a la entidad
    assertNotNull(entity);
    assertEquals(1001L, entity.getBankAccountId());
    assertEquals("ES12 3456 7890 1234 5678 9012", entity.getAccountNumber());
    assertEquals("AHORRO", entity.getAccountType());
    assertEquals(1234.56d, entity.getBalance(), 0.0001d);

    // Asociación ManyToOne resuelta con el Cliente del servicio
    assertNotNull(entity.getClient());
    assertEquals(2002L, entity.getClient().getClientId());
    assertEquals("Alejandro", entity.getClient().getName());
  }

  @Test
  void toCuenta_manejaIdClienteNull_oClienteNoEncontrado() {
    // Arrange: idCliente = null
    CuentaUpdateDTO dtoNullId = new CuentaUpdateDTO();
    dtoNullId.setIdCuenta(1L);
    dtoNullId.setIdCliente(null);
    dtoNullId.setNumeroCuenta("ACC-NULL");
    dtoNullId.setTipoCuenta("CORRIENTE");
    dtoNullId.setSaldo(0.0d);

    when(clienteService.findById(null)).thenReturn(null);

    // Act
    BankAccount entityNullId = mapper.toCuenta(dtoNullId, clienteService);

    // Assert
    assertNotNull(entityNullId);
    assertEquals(1L, entityNullId.getBankAccountId());
    assertEquals("ACC-NULL", entityNullId.getAccountNumber());
    assertEquals("CORRIENTE", entityNullId.getAccountType());
    assertEquals(0.0d, entityNullId.getBalance(), 0.0001d);
    assertNull(entityNullId.getClient(), "Con idCliente null, la asociación debe ser null");

    // Arrange: idCliente con valor pero el servicio no encuentra al cliente
    CuentaUpdateDTO dtoNotFound = new CuentaUpdateDTO();
    dtoNotFound.setIdCuenta(2L);
    dtoNotFound.setIdCliente(999L);
    dtoNotFound.setNumeroCuenta("ACC-404");
    dtoNotFound.setTipoCuenta("AHORRO");
    dtoNotFound.setSaldo(50.0d);

    when(clienteService.findById(999L)).thenReturn(null);

    // Act
    BankAccount entityNotFound = mapper.toCuenta(dtoNotFound, clienteService);

    // Assert
    assertNotNull(entityNotFound);
    assertEquals(2L, entityNotFound.getBankAccountId());
    assertEquals("ACC-404", entityNotFound.getAccountNumber());
    assertEquals("AHORRO", entityNotFound.getAccountType());
    assertEquals(50.0d, entityNotFound.getBalance(), 0.0001d);
    assertNull(entityNotFound.getClient(), "Si el servicio no retorna cliente, la asociación debe ser null");
  }

  @Test
  void toCuenta_manejaCamposNulosEnDTO() {
    // Arrange: DTO con varios nulos
    CuentaUpdateDTO dto = new CuentaUpdateDTO();
    dto.setIdCuenta(null);
    dto.setIdCliente(null);
    dto.setNumeroCuenta(null);
    dto.setTipoCuenta(null);
    
    when(clienteService.findById(null)).thenReturn(null);

    // Act
    BankAccount entity = mapper.toCuenta(dto, clienteService);

    // Assert
    assertNotNull(entity);
    assertNull(entity.getBankAccountId());
    assertNull(entity.getAccountNumber());
    assertNull(entity.getAccountType());
    assertNull(entity.getClient());

    assertEquals(0.0d, entity.getBalance(), 0.0001d);

  }
  
  @Test
  void toCuenta_manejaNulosEnDTO() {
    // Arrange: DTO con varios nulos
    CuentaUpdateDTO dto = null;

    // Act
    BankAccount entity = mapper.toCuenta(dto, clienteService);

    // Assert
    assertNull(entity);
  }
}
