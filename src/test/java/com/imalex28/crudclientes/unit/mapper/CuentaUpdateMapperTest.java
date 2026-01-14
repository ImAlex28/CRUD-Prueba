
package com.imalex28.crudclientes.unit.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.imalex28.crudclientes.dto.account.CuentaUpdateDTO;
import com.imalex28.crudclientes.mapper.CuentaUpdateMapper;
import com.imalex28.crudclientes.model.Cliente;
import com.imalex28.crudclientes.model.Cuenta;
import com.imalex28.crudclientes.service.ClienteService;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CuentaUpdateMapperTest {

  // Instanciamos la implementación generada por MapStruct
  private final CuentaUpdateMapper mapper = Mappers.getMapper(CuentaUpdateMapper.class);

  @Mock
  ClienteService clienteService;

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
    Cliente cliente = new Cliente();
    cliente.setIdCliente(2002L);
    cliente.setNombre("Alejandro");
    when(clienteService.findById(2002L)).thenReturn(cliente);

    // Act
    Cuenta entity = mapper.toCuenta(dto, clienteService);

    // Assert: mapeo correcto a la entidad
    assertNotNull(entity);
    assertEquals(1001L, entity.getIdCuenta());
    assertEquals("ES12 3456 7890 1234 5678 9012", entity.getNumeroCuenta());
    assertEquals("AHORRO", entity.getTipoCuenta());
    assertEquals(1234.56d, entity.getSaldo(), 0.0001d);

    // Asociación ManyToOne resuelta con el Cliente del servicio
    assertNotNull(entity.getIdCliente());
    assertEquals(2002L, entity.getIdCliente().getIdCliente());
    assertEquals("Alejandro", entity.getIdCliente().getNombre());
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
    Cuenta entityNullId = mapper.toCuenta(dtoNullId, clienteService);

    // Assert
    assertNotNull(entityNullId);
    assertEquals(1L, entityNullId.getIdCuenta());
    assertEquals("ACC-NULL", entityNullId.getNumeroCuenta());
    assertEquals("CORRIENTE", entityNullId.getTipoCuenta());
    assertEquals(0.0d, entityNullId.getSaldo(), 0.0001d);
    assertNull(entityNullId.getIdCliente(), "Con idCliente null, la asociación debe ser null");

    // Arrange: idCliente con valor pero el servicio no encuentra al cliente
    CuentaUpdateDTO dtoNotFound = new CuentaUpdateDTO();
    dtoNotFound.setIdCuenta(2L);
    dtoNotFound.setIdCliente(999L);
    dtoNotFound.setNumeroCuenta("ACC-404");
    dtoNotFound.setTipoCuenta("AHORRO");
    dtoNotFound.setSaldo(50.0d);

    when(clienteService.findById(999L)).thenReturn(null);

    // Act
    Cuenta entityNotFound = mapper.toCuenta(dtoNotFound, clienteService);

    // Assert
    assertNotNull(entityNotFound);
    assertEquals(2L, entityNotFound.getIdCuenta());
    assertEquals("ACC-404", entityNotFound.getNumeroCuenta());
    assertEquals("AHORRO", entityNotFound.getTipoCuenta());
    assertEquals(50.0d, entityNotFound.getSaldo(), 0.0001d);
    assertNull(entityNotFound.getIdCliente(), "Si el servicio no retorna cliente, la asociación debe ser null");
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
    Cuenta entity = mapper.toCuenta(dto, clienteService);

    // Assert
    assertNotNull(entity);
    assertNull(entity.getIdCuenta());
    assertNull(entity.getNumeroCuenta());
    assertNull(entity.getTipoCuenta());
    assertNull(entity.getIdCliente());

    assertEquals(0.0d, entity.getSaldo(), 0.0001d);

  }
  
  @Test
  void toCuenta_manejaNulosEnDTO() {
    // Arrange: DTO con varios nulos
    CuentaUpdateDTO dto = null;

    // Act
    Cuenta entity = mapper.toCuenta(dto, clienteService);

    // Assert
    assertNull(entity);
  }
}
