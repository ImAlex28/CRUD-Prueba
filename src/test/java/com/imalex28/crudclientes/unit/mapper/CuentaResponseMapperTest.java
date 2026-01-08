
package com.imalex28.crudclientes.unit.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.imalex28.crudclientes.dto.account.CuentaResponseDTO;
import com.imalex28.crudclientes.mapper.CuentaResponseMapper;
import com.imalex28.crudclientes.model.Cliente;
import com.imalex28.crudclientes.model.Cuenta;

public class CuentaResponseMapperTest {

  // Instanciamos el mapper
  private final CuentaResponseMapper mapper = Mappers.getMapper(CuentaResponseMapper.class);

  @Test
  void toCuentaResponseDTO_mapeaTodosLosCampos() {
    // Arrange
    Cliente cliente = new Cliente();
    cliente.setIdCliente(2002L);
    cliente.setNombre("Alejandro");

    Cuenta entity = new Cuenta();
    entity.setIdCuenta(1001L);
    entity.setIdCliente(cliente); 
    entity.setNumeroCuenta("ES12 3456 7890 1234 5678 9012");
    entity.setTipoCuenta("AHORRO"); 
    entity.setSaldo(1234.56d); 

    // Act
    CuentaResponseDTO dto = mapper.toCuentaResponseDTO(entity);

    // Assert
    assertNotNull(dto);
    assertEquals(1001L, dto.getIdCuenta());
    assertEquals(entity.getIdCliente(), dto.getCliente()); // <- id del cliente
    assertEquals("ES12 3456 7890 1234 5678 9012", dto.getNumeroCuenta());
    assertEquals("AHORRO", dto.getTipoCuenta());
    assertEquals(1234.56d, dto.getSaldo(), 0.0001d);

  }

  @Test
  void toCuentaResponseDTO_manejaNulos() {
    Cuenta entity = new Cuenta();
    entity.setIdCuenta(null);
    entity.setIdCliente(null);
    entity.setNumeroCuenta(null);
    entity.setTipoCuenta(null);
    // entity.setSaldo(...) 

    CuentaResponseDTO dto = mapper.toCuentaResponseDTO(entity);

    assertNotNull(dto);
    assertNull(dto.getIdCuenta());
    assertNull(dto.getCliente());
    assertNull(dto.getNumeroCuenta());
    assertNull(dto.getTipoCuenta());
    assertEquals(0.0d, dto.getSaldo(), 0.0d);
  }
  
  @Test
  void toCuentaResponseDTO_manejaDTONulo() {
    Cuenta entity = null;

    CuentaResponseDTO dto = mapper.toCuentaResponseDTO(entity);

    assertNull(dto);
  }

  @Test
  void toCuentaResponseDTOList_mapeaColeccion() {
    Cliente c1 = new Cliente(); c1.setIdCliente(10L);
    Cliente c2 = new Cliente(); c2.setIdCliente(20L);

    Cuenta a = new Cuenta();
    a.setIdCuenta(1L);
    a.setIdCliente(c1);
    a.setNumeroCuenta("ACC-001");
    a.setTipoCuenta("CORRIENTE");
    a.setSaldo(100.00d);

    Cuenta b = new Cuenta();
    b.setIdCuenta(2L);
    b.setIdCliente(c2);
    b.setNumeroCuenta("ACC-002");
    b.setTipoCuenta("AHORRO");
    b.setSaldo(250.50d);

    List<CuentaResponseDTO> list = mapper.toCuentaResponseDTOList(List.of(a, b));

    assertNotNull(list);
    assertEquals(2, list.size());

    assertEquals(1L, list.get(0).getIdCuenta());
    assertEquals(a.getIdCliente(), list.get(0).getCliente());
    assertEquals("ACC-001", list.get(0).getNumeroCuenta());
    assertEquals("CORRIENTE", list.get(0).getTipoCuenta());
    assertEquals(100.00d, list.get(0).getSaldo(), 0.0001d);

    assertEquals(2L, list.get(1).getIdCuenta());
    assertEquals(b.getIdCliente(), list.get(1).getCliente());
    assertEquals("ACC-002", list.get(1).getNumeroCuenta());
    assertEquals("AHORRO", list.get(1).getTipoCuenta());
    assertEquals(250.50d, list.get(1).getSaldo(), 0.0001d);
  }

  @Test
  void toCuentaResponseDTOList_manejaListaVacia() {
    List<CuentaResponseDTO> list = mapper.toCuentaResponseDTOList(List.of());
    assertNotNull(list);
    assertTrue(list.isEmpty());
  }
  
  @Test
  void toCuentaResponseDTOList_manejaNull() {
    List<CuentaResponseDTO> list = mapper.toCuentaResponseDTOList(null);
    assertNull(list);
  }
  
}
