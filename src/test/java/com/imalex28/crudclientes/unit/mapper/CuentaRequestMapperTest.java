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
import com.imalex28.crudclientes.mapper.CuentaRequestMapper;
import com.imalex28.crudclientes.model.Cliente;
import com.imalex28.crudclientes.model.Cuenta;
import com.imalex28.crudclientes.service.ClienteService;

public class CuentaRequestMapperTest {
	
	  // Instanciamos el mapper
	  private final CuentaRequestMapper mapper = Mappers.getMapper(CuentaRequestMapper.class);
	  

	  @Mock
	  ClienteService clienteService;


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
		    Cliente cliente = new Cliente();
		    cliente.setIdCliente(123L);
		    cliente.setNombre("Alejandro");
		    cliente.setApellidos("Fernandez");
		    cliente.setEmail("alejandro@example.com");

		    when(clienteService.findById(123L)).thenReturn(cliente);

		    // Act
		    Cuenta entity = mapper.toCuenta(dto, clienteService);

		    // Assert: mapeo de campos simples
		    assertNotNull(entity);
		    assertEquals("ES12 3456 7890 1234 5678 9012", entity.getNumeroCuenta());
		    assertEquals("AHORRO", entity.getTipoCuenta());
		    assertEquals(1500.75, entity.getSaldo());

		    // Assert: id autogenerado ignorado (debe quedar null)
		    assertNull(entity.getIdCuenta(), "idCuenta debe permanecer null (autogenerado)");

		    // Assert: ManyToOne resuelto correctamente
		    assertNotNull(entity.getCliente());
		    assertEquals(123L, entity.getCliente().getIdCliente());
		    assertEquals("Alejandro", entity.getCliente().getNombre());

		    // Verifica la interacción con el servicio
		    verify(clienteService, times(1)).findById(123L);
		    verifyNoMoreInteractions(clienteService);

	  }

	  @Test
	   void toCuenta_manejaDtoNull() {

		  // Arrange
		  CuentaRequestDTO dto = null;

		  // Act
		  Cuenta entity = mapper.toCuenta(dto, clienteService);

		  // Assert
		  assertNull(entity);

	   }
}
