package csv_to_sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
		 *	test();
		 */
		
		
		delete("tabla");
		
		/*
		 * 3 Funciones principales con nombres autodescriptivos
		 */
		/*
		if(option.equals("-i")) insert();
		if(option.equals("-u")) update();
		if(option.equals("-d")) delete();
		*/
	}
	
	public static void insert(String tablename) throws IOException {
		String filename = "insert_"+tablename+".sql";
		String query = "INSERT into "+tablename+" (";
		try {
			Reader reader = Files.newBufferedReader(Paths.get("/home/openbravo/Escritorio/ejemplo.csv"));
			CSVReader csvreader = new CSVReader(reader);
			String[] array;
			String[] campos;
			String consulta ="";
			boolean flag=false;
			while((array = csvreader.readNext()) != null) {
				for(String s : array) {
					String[] splited = s.split(";");
					if(!flag) {
						campos = splited;
						flag=true;
						int counter =0;
						for(String str : campos) {
							if(counter!=splited.length-1) {
								query +=  str+", ";
								counter++;
							}
							else {
								query+= str+")";
							}
						}
						continue;
					}
					else {
						consulta+= query+" values (";
						int counter =0;
						for(String s2: splited) {
							if(counter!=splited.length-1) {
								consulta +=  "'"+s2+"', ";
								counter++;
							}
							else consulta+= "'"+s2+"');\n";
						}
						
					}
					
				}
			}
			File f = new File("/home/openbravo/Escritorio/"+filename);
			FileWriter fw = new FileWriter(f);
			fw.write(consulta);
			reader.close();
			csvreader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void delete(String tablename) throws IOException {
		String filename = "delete_"+tablename+".sql";
		String query = "Delete from "+tablename+" where ";
		try {
			Reader reader = Files.newBufferedReader(Paths.get("/home/openbravo/Escritorio/ejemplo.csv"));
			CSVReader csvreader = new CSVReader(reader);
			String[] array;
			String[] campos = null;
			String consulta ="";
			boolean flag=false;
			while((array = csvreader.readNext()) != null) {
				for(String s : array) {
					String[] splited = s.split(";");
					if(!flag) {
						campos = splited;
						flag=true;
						continue;
					}
					else {
						consulta+= query;
						int counter =0;
						for(String s2: splited) {
							if(counter!=splited.length-1) {
								consulta +=  campos[counter]+" = '"+s2+"', ";
								counter++;
							}
							else consulta+= campos[counter]+" = '"+s2+"';\n";
						}
						
					}
					
				}
			}
			File f = new File("/home/openbravo/Escritorio/"+filename);
			FileWriter fw = new FileWriter(f);
			fw.write(consulta);
			reader.close();
			csvreader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	/*Funciona 
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
	}*/
}
