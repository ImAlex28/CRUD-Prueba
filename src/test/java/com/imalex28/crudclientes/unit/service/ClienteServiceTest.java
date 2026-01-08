package com.imalex28.crudclientes.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.imalex28.crudclientes.model.Cliente;
import com.imalex28.crudclientes.repository.ClienteRepository;
import com.imalex28.crudclientes.service.ClienteService;

import jakarta.ws.rs.NotFoundException;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {
	

	  @Mock
	  ClienteRepository clienteRepository;

	  @InjectMocks
	  ClienteService service;


	  // ---------- findAll ----------
	  @Test
	  void findAll_devuelveListaDelRepositorio() {
		Cliente c1 = new Cliente();
		Cliente c2 = new Cliente();
		c1.setIdCliente(1L);
		c2.setIdCliente(2L);
		
		when(clienteRepository.findAll()).thenReturn(List.of(c1,c2));
		
		List<Cliente> result = service.findAll();
		
		assertNotNull(result);
		assertEquals(1L,result.get(0).getIdCliente());
		assertEquals(2L,result.get(1).getIdCliente());
		verify(clienteRepository, times(1)).findAll();
		verifyNoMoreInteractions(clienteRepository);
	}
	  
	  // ---------- findById (OK) ----------
	  @Test
	  void findById_ok_devuelveCliente() {
		  Cliente cliente = new Cliente();
		  cliente.setIdCliente(1L);
		  
		  when(clienteRepository.findById(1L)).thenReturn(cliente);
		  
		  Cliente result = service.findById(1L);
		  
		  assertNotNull(result);
		  assertEquals(1L, cliente.getIdCliente());
		  verify(clienteRepository, times(1)).findById(1L);
		  verifyNoMoreInteractions(clienteRepository);
	  }
	  
	  // ---------- findById (KO) ----------
	  @Test
	  void findById_ko_clienteNotFound() {
		  Cliente cliente = new Cliente();
		  cliente.setIdCliente(999L);
		  
		  when(clienteRepository.findById(999L)).thenReturn(null);
		  
	    NotFoundException ex = assertThrows(NotFoundException.class,
		        () -> service.findById(999L));

		  assertEquals("Cliente con ID 999 no encontrado", ex.getMessage());
		  verify(clienteRepository, times(1)).findById(999L);
		  verifyNoMoreInteractions(clienteRepository);
	  }
	  
	  // ---------- save ---------- 
	  @Test
	  void save_delegaEnElRepo() {
		  Cliente cliente = new Cliente();
		  cliente.setIdCliente(1L);
		  cliente.setEmail("ejemplo@ejemplo.com");
		  
		  service.save(cliente);
		  
		  verify(clienteRepository, times(1)).save(cliente);
		  verify(clienteRepository, times(1)).findByEmail(any());
	      verifyNoMoreInteractions(clienteRepository);

	  }
	  
	  // ---------- update (OK) ---------- 
	  @Test
	  void update_ok_actualiza() {
		  Cliente cliente = new Cliente();
		  cliente.setIdCliente(1L);
		  when(clienteRepository.findById(1L)).thenReturn(cliente);
		  
		  service.update(cliente);
		  
		  verify(clienteRepository, times(1)).update(cliente);
	      verifyNoMoreInteractions(clienteRepository);
		  
	  }
	  
	  // ---------- update (KO) ---------- 
	  @Test
	  void update_ko_noExisteCliente() {
		  Cliente cliente = new Cliente();
		  cliente.setIdCliente(999L);
		  when(clienteRepository.findById(999L)).thenReturn(null);

		  NotFoundException ex = assertThrows(NotFoundException.class, () -> service.update(cliente));
		  assertTrue(ex.getMessage().contains("Cliente con ID 999 no encontrado"));

		  verify(clienteRepository).findById(999L);
		  verify(clienteRepository, never()).update(any());
	      verifyNoMoreInteractions(clienteRepository);
		  
	  }
	  

	  // ---------- delete (OK) ----------
	  @Test
	  void delete_existeElimina() {
	    when(clienteRepository.findById(77L)).thenReturn(new Cliente());

	    service.delete(77L);

	    verify(clienteRepository).findById(77L);
	    verify(clienteRepository).delete(77L);
	    verifyNoMoreInteractions(clienteRepository);
	  }

	  // ---------- delete (KO) ----------
	  @Test
	  void delete_noExiste_lanzaNotFoundException_yNoElimina() {
	    when(clienteRepository.findById(77L)).thenReturn(null);

	    NotFoundException ex = assertThrows(NotFoundException.class, () -> service.delete(77L));
	    assertTrue(ex.getMessage().contains("Cliente con ID 77 no encontrado"));

	    verify(clienteRepository).findById(77L);
	    verify(clienteRepository, never()).delete(anyLong());
	    verifyNoMoreInteractions(clienteRepository);
	  }


}
