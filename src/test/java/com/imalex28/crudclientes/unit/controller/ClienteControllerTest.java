package com.imalex28.crudclientes.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.imalex28.crudclientes.controller.ClienteController;
import com.imalex28.crudclientes.dto.account.CuentaResponseDTO;
import com.imalex28.crudclientes.dto.client.ClienteRequestDTO;
import com.imalex28.crudclientes.dto.client.ClienteResponseDTO;
import com.imalex28.crudclientes.dto.client.ClienteUpdateDTO;
import com.imalex28.crudclientes.mapper.BankAccountRequestMapper;
import com.imalex28.crudclientes.mapper.BankAccountResponseMapper;
import com.imalex28.crudclientes.mapper.ClientRequestMapper;
import com.imalex28.crudclientes.mapper.ClientResponseMapper;
import com.imalex28.crudclientes.mapper.ClientUpdateMapper;
import com.imalex28.crudclientes.model.BankAccount;
import com.imalex28.crudclientes.model.Client;
import com.imalex28.crudclientes.service.BankAccountService;
import com.imalex28.crudclientes.service.ClientService;
import jakarta.ws.rs.core.Response;

@ExtendWith(MockitoExtension.class)
public class ClienteControllerTest {
	    @Mock BankAccountService cuentaService;
	    @Mock ClientService clienteService;
	    @Mock BankAccountRequestMapper cuentaRequestMapper;
	    @Mock BankAccountResponseMapper cuentaResponseMapper;
	    @Mock ClientUpdateMapper clienteUpdateMapper;
	    @Mock ClientRequestMapper clienteRequestMapper;
	    @Mock ClientResponseMapper clienteResponseMapper;

	    @InjectMocks
	    ClienteController controller;

	    @Test
	    void listAll_devuelve_lista_dtos() {
	        Client c1 = new Client(); c1.setClientId(1L);
	        Client c2 = new Client(); c2.setClientId(2L);
	        when(clienteService.findAll()).thenReturn(List.of(c1, c2));

	        ClienteResponseDTO dto1 = new ClienteResponseDTO(); dto1.setId(1L);
	        ClienteResponseDTO dto2 = new ClienteResponseDTO(); dto2.setId(2L);
	        when(clienteResponseMapper.toClienteResponseDTOList(List.of(c1, c2)))
	                .thenReturn(List.of(dto1, dto2));

	        List<ClienteResponseDTO> result = controller.listAll();

	        assertNotNull(result);
	        assertEquals(2, result.size());
	        assertEquals(1L, result.get(0).getId());
	        assertEquals(2L, result.get(1).getId());
	        verify(clienteService).findAll();
	        verify(clienteResponseMapper).toClienteResponseDTOList(List.of(c1, c2));
	    }

	   @Test
	    void findById_mapea_corretamente() {
	        Long id = 99L;
	        Client cliente = new Client(); cliente.setClientId(id);
	        when(clienteService.findById(id)).thenReturn(cliente);

	        ClienteResponseDTO dto = new ClienteResponseDTO(); dto.setId(id);
	        when(clienteResponseMapper.toClienteResponseDTO(cliente)).thenReturn(dto);

	        ClienteResponseDTO result = controller.findById(id);

	        assertNotNull(result);
	        assertEquals(id, result.getId());
	        verify(clienteService).findById(id);
	        verify(clienteResponseMapper).toClienteResponseDTO(cliente);
	    }
	   
	    @Test
	    void findById_si_service_lanza_excepcion_se_propaga() {
	        Long id = 123L;
	        when(clienteService.findById(id)).thenThrow(new RuntimeException("no encontrado"));

	        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.findById(id));
	        assertEquals("no encontrado", ex.getMessage());
	    }
	    
	   @Test
	    void create_usa_mapper_y_service_devuelve_201() {
		    ClienteRequestDTO req = new ClienteRequestDTO();
		    Client cliente = new Client();
		    cliente.setClientId(123L);
	        when(clienteRequestMapper.toCliente(req)).thenReturn(cliente);

	        Response resp = controller.create(req);

	        assertNotNull(resp);
	        assertEquals(201, resp.getStatus());
	        verify(clienteRequestMapper).toCliente(req);
	        verify(clienteService).save(cliente);
	    }

	   @Test
	    void update_usa_mapper_y_service_devuelve_200() {
	        ClienteUpdateDTO req = new ClienteUpdateDTO();
	        Client cliente = new Client();
	        when(clienteUpdateMapper.toCliente(req)).thenReturn(cliente);

	        Response resp = controller.update(req);

	        assertNotNull(resp);
	        assertEquals(200, resp.getStatus());
	        verify(clienteUpdateMapper).toCliente(req);
	        verify(clienteService).update(cliente);
	    }

	    @Test
	    void delete_llama_service_y_devuelve_204() {
	        Response resp = controller.delete(5L);

	        assertNotNull(resp);
	        assertEquals(204, resp.getStatus());
	        verify(clienteService).delete(5L);
	    }
	    
	    @Test
	    void findCuentasCliente_devuelveDTOCuentas() {
	    	Client cliente = new Client(); cliente.setClientId(2L);
	    	BankAccount c1 = new BankAccount(); c1.setBankAccountId(1L); c1.setClient(cliente);
	    	BankAccount c2 = new BankAccount(); c2.setBankAccountId(2L); c2.setClient(cliente);
	    	
	    	when(cuentaService.findByIdCliente(cliente.getClientId())).thenReturn(List.of(c1,c2));
	    	
	    	CuentaResponseDTO dto1 = new CuentaResponseDTO(); dto1.setCliente(cliente);dto1.setIdCuenta(1L);
	    	CuentaResponseDTO dto2 = new CuentaResponseDTO(); dto2.setCliente(cliente);dto2.setIdCuenta(2L);
	    	
	    	when(cuentaResponseMapper.toCuentaResponseDTOList(List.of(c1,c2))).thenReturn(List.of(dto1,dto2));
	    	
	    	List<CuentaResponseDTO> resp = controller.findCuentasCliente(cliente.getClientId());
	    	
	    	assertNotNull(resp);
	    	assertEquals(2,resp.size());
	    	assertEquals(1L,resp.get(0).getIdCuenta());
	    	assertEquals(2L,resp.get(0).getCliente().getClientId());
	    	assertEquals(2L,resp.get(1).getIdCuenta());
	    	assertEquals(2L,resp.get(1).getCliente().getClientId());
	    	verify(cuentaService).findByIdCliente(cliente.getClientId());
	    	verify(cuentaResponseMapper).toCuentaResponseDTOList(List.of(c1,c2));
	    		
	    }
	    
	    @Test
	    void saveCuenta_invocaService_devuelve201_y_cuenta() {
	        Long idPath = 1L;
	        BankAccount cuenta = new BankAccount(); cuenta.setBankAccountId(10L);
	    	
	        Response resp = controller.saveCuenta(idPath,cuenta);
	        

	        assertNotNull(cuenta.getClient(), "El controller debe asociar un Cliente en cuenta.idCliente");
	        assertEquals(idPath, cuenta.getClient().getClientId());


	        assertNotNull(resp);
	        assertEquals(201, resp.getStatus());
	        assertEquals(cuenta, resp.getEntity());
	        verify(cuentaService).save(cuenta);
	    }
	    
	    @Test
	    void getSaldoTotal_invocaService_devuelve200_y_saldo() {
	    	Long id = 1L;
	    	Double saldo = 12563.92;
	    	
	        when(cuentaService.getSaldoTotalByCliente(id)).thenReturn(saldo);

	    	
	        Response resp = controller.getSaldoTotal(id);
	  

	        assertNotNull(resp);
	        assertEquals(200, resp.getStatus());
	        verify(cuentaService).getSaldoTotalByCliente(id);
	        

	        assertNotNull(resp.getEntity());
	        assertInstanceOf(java.util.Map.class, resp.getEntity());

	        @SuppressWarnings("unchecked")
			Map<String, Object> body = (Map<String, Object>) resp.getEntity();
	        
	        assertEquals(1, body.size());
	        assertEquals(saldo, body.get("saldoCliente"));
	    }

}
