package csv_to_sql;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;

public class reader {
	
	public static void main(String[] args) throws IOException {
		/*
		 * Clase que convierte csv separados por ";" en scripts sql para automatizar el trabajo con los clientes
		 * La clase recive por comando la opcion y el nombre de la tabla.
		 * El nombre del script sera siempre (opcion)_(nombre tabla).sql
		 * 
		 */
		
		/*
		 * Funcion para testear cosas.
		 * Porfavor meter las cosas nuevas o que no funcionen en esta clase o usar debugger.
		 */
		test();
		
		String option = args.toString();
		
		/*
		 * 3 Funciones principales con nombres autodescriptivos
		 */
		/*
		if(option.equals("-i")) insert();
		if(option.equals("-u")) update();
		if(option.equals("-d")) delete();
		*/
	}
	
	//Funciona 
	public static void test() throws IOException {
		try {
			Reader reader = Files.newBufferedReader(Paths.get("/home/openbravo/Escritorio/ejemplo.csv"));
			CSVReader csvreader = new CSVReader(reader);
			String[] array;
			while((array = csvreader.readNext()) != null) {
				for(String s : array) {
					System.out.println(s);
				}
			}
			reader.close();
			csvreader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
