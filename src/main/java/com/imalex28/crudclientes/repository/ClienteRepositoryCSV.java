package com.imalex28.crudclientes.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import com.imalex28.crudclientes.model.Cliente;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@ApplicationScoped 
@Named("csv")
public class ClienteRepositoryCSV implements ClienteRepository{
	
	private static final String FILE_PATH="Clientes.csv";
	
	private void escribirCSV(List<Cliente> clientes) {
	    try (BufferedWriter bw = Files.newBufferedWriter(java.nio.file.Path.of(FILE_PATH))) {
	        // Escribir cabecera
	        bw.write("idCliente,nombre,apellido,dni,email");
	        bw.newLine();

	        // Escribir la linea de cada cliente
	        for (Cliente c : clientes) {
	            String linea = String.format("%d,%s,%s,%s,%s",
	                    c.getIdCliente(),
	                    c.getNombre(),
	                    c.getApellidos(),
	                    c.getDni(),
	                    c.getEmail());
	            bw.write(linea);
	            bw.newLine();
	        }

	    } catch (IOException e) {
	        e.printStackTrace();  //por si falla algo, saberlo
	    }
	}

	@Override
	public List<Cliente> findAll() {
		List<Cliente> clientes = new ArrayList<>();
		try (BufferedReader br = Files.newBufferedReader(java.nio.file.Path.of(FILE_PATH))) {
		        br.readLine(); // saltar cabecera que tiene los nombres de columnas
		        String line; // Almacena la línea que se extrae del CSV
		
		        while ((line = br.readLine()) != null) {
		            String[] data = line.split(","); //separamos por comas, que es el separador que tiene este csv . Se almacena en una colección de Strings
		            clientes.add(new Cliente( //Agregamos a la Lista el objeto cliente que construimos usando los datos de la línea del CSV
		                    Long.parseLong(data[0]), //idCliente
		                    data[1], //nombre
		                    data[2], //apellido
		                    data[3], //dni
		                    data[4] //email
		            ));
		        }
		    } catch (IOException e) {
		        e.printStackTrace(); //por si falla algo, saberlo
		    }
		
		   return clientes;
	}

	@Override
	public Cliente findById(Long id) {
		Cliente cliente;
		
		List<Cliente> clientes = findAll(); // llamo a la función findAll para no duplicar codigo 
		
		cliente = clientes.stream().filter(c -> c.getIdCliente().equals(id)).findFirst().orElse(null); // Del Stream sacamos el cliente que tiene el mismo id que se pasó en la petición
		
		return cliente;
	}

	@Override
	public void save(Cliente cliente) {
		List<Cliente> clientes = findAll(); // llamo a la función findAll para no duplicar codigo 
		
	    // Generar ID incremental
	    long nuevoId = 1;
	    
	    if (!clientes.isEmpty()) {
	        nuevoId = clientes.stream()
	                          .mapToLong(Cliente::getIdCliente)
	                          .max()
	                          .getAsLong() + 1; //obtenemos con esto el siguiente ID secuencial
	    }
	    cliente.setIdCliente(nuevoId); // si ha cambiado, se almacena el nuevo. Si no ha cambiado, será el 1

	    // Agregar el cliente nuevo a la lista
	    clientes.add(cliente);
	    
	    escribirCSV(clientes);
		
	}

	@Override
	public void update(Cliente cliente) {
		
		List<Cliente> clientes = findAll(); // Almacenamos otra vez todos los clientes
				
	    // Reemplazar el cliente con el mismo ID
	    for (int i = 0; i < clientes.size(); i++) {
	        if (clientes.get(i).getIdCliente().equals(cliente.getIdCliente())) {
	            clientes.set(i, cliente);
	            break;
	        }
	    }

	    escribirCSV(clientes);
		
	}

	@Override
	public void delete(Long id) {
		
		List<Cliente> clientes = findAll(); // Almacenamos otra vez todos los clientes
		
	    // Eliminar el cliente con el mismo ID
	    clientes.removeIf(c -> c.getIdCliente().equals(id));
	    
	    escribirCSV(clientes);
		
	}

	@Override
	public boolean existsById(Long idCliente) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
