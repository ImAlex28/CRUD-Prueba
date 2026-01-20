
package com.imalex28.crudclientes.unit.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.imalex28.crudclientes.dto.account.CuentaResponseDTO;
import com.imalex28.crudclientes.mapper.BankAccountResponseMapper;
import com.imalex28.crudclientes.model.Client;
import com.imalex28.crudclientes.model.BankAccount;

public class CuentaResponseMapperTest {

  // Instanciamos el mapper
  private final BankAccountResponseMapper mapper = Mappers.getMapper(BankAccountResponseMapper.class);

  @Test
  void toCuentaResponseDTO_mapeaTodosLosCampos() {
    // Arrange
    Client cliente = new Client();
    cliente.setClientId(2002L);
    cliente.setName("Alejandro");

    BankAccount entity = new BankAccount();
    entity.setBankAccountId(1001L);
    entity.setClient(cliente); 
    entity.setAccountNumber("ES12 3456 7890 1234 5678 9012");
    entity.setAccountType("AHORRO"); 
    entity.setBalance(1234.56d); 

    // Act
    CuentaResponseDTO dto = mapper.toCuentaResponseDTO(entity);

    // Assert
    assertNotNull(dto);
    assertEquals(1001L, dto.getIdCuenta());
    assertEquals(entity.getClient(), dto.getCliente()); // <- id del cliente
    assertEquals("ES12 3456 7890 1234 5678 9012", dto.getNumeroCuenta());
    assertEquals("AHORRO", dto.getTipoCuenta());
    assertEquals(1234.56d, dto.getSaldo(), 0.0001d);

  }

  @Test
  void toCuentaResponseDTO_manejaNulos() {
	BankAccount entity = new BankAccount();
    entity.setBankAccountId(null);
    entity.setClient(null);
    entity.setAccountNumber(null);
    entity.setAccountType(null);
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
	BankAccount entity = null;

    CuentaResponseDTO dto = mapper.toCuentaResponseDTO(entity);

    assertNull(dto);
  }

  @Test
  void toCuentaResponseDTOList_mapeaColeccion() {
    Client c1 = new Client(); c1.setClientId(10L);
    Client c2 = new Client(); c2.setClientId(20L);

    BankAccount a = new BankAccount();
    a.setBankAccountId(1L);
    a.setClient(c1);
    a.setAccountNumber("ACC-001");
    a.setAccountType("CORRIENTE");
    a.setBalance(100.00d);

    BankAccount b = new BankAccount();
    b.setBankAccountId(2L);
    b.setClient(c2);
    b.setAccountNumber("ACC-002");
    b.setAccountType("AHORRO");
    b.setBalance(250.50d);

    List<CuentaResponseDTO> list = mapper.toCuentaResponseDTOList(List.of(a, b));

    assertNotNull(list);
    assertEquals(2, list.size());

    assertEquals(1L, list.get(0).getIdCuenta());
    assertEquals(a.getClient(), list.get(0).getCliente());
    assertEquals("ACC-001", list.get(0).getNumeroCuenta());
    assertEquals("CORRIENTE", list.get(0).getTipoCuenta());
    assertEquals(100.00d, list.get(0).getSaldo(), 0.0001d);

    assertEquals(2L, list.get(1).getIdCuenta());
    assertEquals(b.getClient(), list.get(1).getCliente());
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
