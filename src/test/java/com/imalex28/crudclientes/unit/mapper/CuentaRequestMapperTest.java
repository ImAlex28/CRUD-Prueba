package com.imalex28.crudclientes.unit.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;

import com.imalex28.crudclientes.dto.account.CuentaRequestDTO;
import com.imalex28.crudclientes.mapper.BankAccountRequestMapper;
import com.imalex28.crudclientes.model.Client;
import com.imalex28.crudclientes.model.BankAccount;
import com.imalex28.crudclientes.service.ClientService;

public class CuentaRequestMapperTest {
	
	  // Instanciamos el mapper
	  private final BankAccountRequestMapper mapper = Mappers.getMapper(BankAccountRequestMapper.class);
	  

	  @Mock
	  ClientService clienteService;


	  @Test
	  @ExtendWith(MockitoExtension.class)
	  void toCuenta_mapeaCampos() {

		    // Arrange: DTO con todos los campos
		    CuentaRequestDTO dto = new CuentaRequestDTO();
		    dto.setIdCliente(123L);
		    dto.setNumeroCuenta("ES12 3456 7890 1234 5678 9012");
		    dto.setTipoCuenta("AHORRO");
		    dto.setSaldo(1500.75);

		    // Mock: el servicio devuelve un Cliente para ese id
		    Client cliente = new Client();
		    cliente.setClientId(123L);
		    cliente.setName("Alejandro");
		    cliente.setSurname("Fernandez");
		    cliente.setEmail("alejandro@example.com");

		    when(clienteService.findById(123L)).thenReturn(cliente);

		    // Act
		    BankAccount entity = mapper.toCuenta(dto, clienteService);

		    // Assert: mapeo de campos simples
		    assertNotNull(entity);
		    assertEquals("ES12 3456 7890 1234 5678 9012", entity.getAccountNumber());
		    assertEquals("AHORRO", entity.getAccountType());
		    assertEquals(1500.75, entity.getBalance());

		    // Assert: id autogenerado ignorado (debe quedar null)
		    assertNull(entity.getBankAccountId(), "idCuenta debe permanecer null (autogenerado)");

		    // Assert: ManyToOne resuelto correctamente
		    assertNotNull(entity.getClient());
		    assertEquals(123L, entity.getClient().getClientId());
		    assertEquals("Alejandro", entity.getClient().getName());

		    // Verifica la interacción con el servicio
		    verify(clienteService, times(1)).findById(123L);
		    verifyNoMoreInteractions(clienteService);

	  }

	  @Test
	   void toCuenta_manejaDtoNull() {

		  // Arrange
		  CuentaRequestDTO dto = null;

		  // Act
		  BankAccount entity = mapper.toCuenta(dto, clienteService);

		  // Assert
		  assertNull(entity);

	   }
}
