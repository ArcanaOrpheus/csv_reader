package csv_to_sql;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import au.com.bytecode.opencsv.CSVReader;

public class reader{
	

	static public Scanner scanner = new Scanner(System.in);
	
	public static void main(String[] args) throws Exception {
		/*
		 * Creador: Daniel Suñe Cea
		 * Clase que convierte csv separados por ";" en scripts sql para automatizar el trabajo con los clientes
		 * La clase recive por comando la opcion y el nombre de la tabla.
		 * El nombre del script sera siempre (opcion)_(nombre tabla).sql
		 * Funcion para testear cosas.
		 * Porfavor meter las cosas nuevas o que no funcionen en esta clase o usar debugger.
		 *	test();
		 * 3 Funciones principales con nombres autodescriptivos
		 * insert(String table, String path);
		 * update(String table, String path);
		 * delete(String table, String path); 
		 * 
		 * 
		 * 
		 * Variables:
		 * String path: String que contiene el path al csv. Tiene un booleano (pathcheck) que comprueva si han pasado el path
		 * String option: String que contiene la opcion (insert, update o delete). Tiene un booleano (optioncheck) para comprovar si han pasado opcion
		 * String tablename: String que contiene el nombre de la tabla. Tiene un booleano (tablecheck) para comprovar si se ha pasado el nombre de una tabla
		 * 
		 */
		String path = "";
		boolean pathcheck=false;
		String option = "";
		boolean optioncheck=false;
		String tablename="";
		boolean tablecheck=false;
		//Inicio de las comprovaciones
		if(args.length==0) {
			/*
			 * Si no se ha pasado ningun parametro se activa este if.  Todos los booleanos se quedan false ya que no es necesario mas comprovaciones
			 */
			System.out.println("There are no arguments for the method, starting manual input");
			System.out.println("Introduce path to file: ");
			path= scanner.nextLine();
			System.out.println("Introduce option: ");
			option = scanner.nextLine();
			System.out.print("Introduce table name: ");
			tablename = scanner.nextLine();
		}
		else {
			/*
			 * En caso de que al menos exista un parametro se entra en este else donde se recurre a los booleanos y varios if para comprovar que argumentos faltan
			 */
			for(int i=0; i<args.length;i++) {
				String argumento = args[i].substring(0, 2);
				if(argumento.equals("-f")) {
					path=args[i].substring(3);
					pathcheck=true;
				}
				if(argumento.equals("-t")) {
					tablename=args[i].substring(3);
					tablecheck =true;
				}
				/*
				 * La opcion de update,delete o insert se ha modificiado de forma de que se pueda escribir via guion o via string (el nombre de la funcion en si)
				 */
				if( (args[i].equals("-u")) || (args[i].equals("-i")) || (args[i].equals("-d")) ) {
					optioncheck=true;
				}
				if( (args[i].equals("update")) || (args[i].equals("insert")) || (args[i].equals("delete")) ) {
					optioncheck=true;
				}
			}
			/*
			 * Se compruevan los booleanos y se preguntan todos los parametros que faltan.
			 */
			if(!pathcheck) {
				pathcheck=true;
				System.out.println("Introduce path to file: ");
				path= scanner.next();
			}
			if(!optioncheck) {
				optioncheck=true;
				System.out.println("Introduce option: ");
				option = scanner.next();
			}
			if(!tablecheck) {
				System.out.print("Introduce table name: ");
				tablename = scanner.next();
			}
		}
		/*
		 * Estos if simplemente llaman a la opcion que requiere el usuario
		 */
		if(option.equals("-i") || option.equals("insert")) insert(tablename, path);
		if(option.equals("-u") || option.equals("update")) update(tablename, path);
		if(option.equals("-d") || option.equals("delete")) delete(tablename, path);
		scanner.close();
	}
	
	public static void insert(String tablename, String path) throws IOException {
		/*
		 * Funcion insert(String tablename, String path);
		 * Se crean al principio el inicio de la query aprovechando el nombre de la tabla y el nombre del archivo
		 */
		String filename = "insert_"+tablename+".sql";
		String query = "INSERT into "+tablename+" (";
		try {
			/*
			 * Parametros:
			 * Reader: simple reader requerido por CSVReader, recibe el path al csv
			 * csvreader: autodescriptivo. Es el objeto que leera el propio csv
			 * String[] array: Parametro donde guardaremos los diferentes parametros que nos pasan en el csv
			 * String[] campos: Parametro donde guardaremos la primera linea del csv que contiene los campos de la base de datos que queremos modificar
			 * String consulta: String donde guardaremos toda la consulta y sera lo que devolvamos al usuario en el fichero
			 * Boolean flag: Un booleano para ver si hemos pasado por la primera linea o no.
			 */
			Reader reader = Files.newBufferedReader(Paths.get(path));
			CSVReader csvreader = new CSVReader(reader);
			String[] array;
			String[] campos;
			String consulta ="";
			boolean flag=false;
			/*
			 * En el bucle leeremos cada linea, se asignaran los valores a sus campos correspondientes y se guardara la consulta.
			 */
			while((array = csvreader.readNext()) != null) {
				for(String s : array) {
					String[] splited = s.split(";");
					if(!flag) {
						/*
						 * En la primera iteracion encontraremos los campos que queremos insertar y los guardaremos en la query.
						 * La query no se modificara ya que todos los campos seran identicos para cada iteracion
						 */
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
						/*
						 * A partir de la segunda iteracion la flag se vuelve true y todos los valores se usaran para los values.
						 */
						consulta+= query+" values (";
						int counter =0;
						for(String s2: splited) {
							/*
							 * Se usa un if para diferenciar si es el ultimo campo o no de forma que se cierre la consulta.
							 */
							if(counter!=splited.length-1) {
								consulta +=  "'"+s2+"', ";
								counter++;
							}
							else consulta+= "'"+s2+"');\n";
						}
						
					}
					
				}
			}
			/*
			 * Se crea el nuevo archivo con nombre en base a la opcion y el nombre de la tabla.
			 * Luego de escribe la consulta en el archivo  y se cierran todos los readers y writers
			 */
			File f = new File("./"+filename);
			FileWriter fw = new FileWriter(f);
			fw.write(consulta);
			fw.close();
			reader.close();
			csvreader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void delete(String tablename, String path) throws IOException {
		String filename = "delete_"+tablename+".sql";
		String query = "Delete from "+tablename+" where ";
		try {
			Reader reader = Files.newBufferedReader(Paths.get(path));
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
			File f = new File("./"+filename);
			FileWriter fw = new FileWriter(f);
			fw.write(consulta);
			fw.close();
			reader.close();
			csvreader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void update(String tablename, String path) throws Exception{
		/*
		 * Parametros:
		 * String filename: Se crea el nombre del archivo en base a la operacion y el nombre de la tabla
		 */
		String filename = "update_"+tablename+".sql";
		try {
			/*
			 * En esta funcion tenemos dos tipos de parametros, los del set y los del where
			 * 
			 * Parametros:
			 * Reader: simple reader requerido por CSVReader, recibe el path al csv
			 * csvreader: autodescriptivo. Es el objeto que leera el propio csv
			 * String[] array: Parametro donde guardaremos los diferentes parametros que nos pasan en el csv
			 * String[] campos: Parametro donde guardaremos la primera linea del csv que contiene los campos de la base de datos que queremos modificar
			 * String output: String donde guardaremos toda la consulta y sera lo que devolvamos al usuario en el fichero
			 * list <Integer> where: Lista donde se guardan las posiciones de las variables que van al where
			 * Boolean flag: Un booleano para ver si hemos pasado por la primera linea o no.
			 * int contadorquery: int que sirve de comprovacion para ver que hay minimo un campo en el set
			 * 
			 */
			Reader reader = Files.newBufferedReader(Paths.get(path));
			CSVReader csvreader = new CSVReader(reader);
			String[] array;
			String[] campos = null;
			List <Integer> where  = new ArrayList<Integer>();
			boolean flag=false;
			String output ="";
			int contadorquery = 0;
			while((array = csvreader.readNext()) != null) {
				/*
				 * Se divide la query en dos: la parte del where y la del set.
				 */
				String query = "UPDATE "+tablename+" set ";
				String querywhere = " where ";
				for(String s : array) {
					String[] splited = s.split(";");
					if(!flag) {
						try {
							/*
							 * En la primera iteracion encontraremos los campos que queremos insertar y los guardaremos en la query.
							 * Se marcaran las posiciones de los campos que corresponden al where
							 */
							flag=true;
							campos = splited;
							contadorquery = splited.length;
							int counter = 0;
							for(String str : splited) {
								if(str.charAt(0)=='*') {
									contadorquery--;
									campos[counter]= str.substring(1);
									where.add(counter);
								}
								counter++;
							}
							/*
							 * Control de errores: 
							 * En caso de que no existan valores para el set devuelve error.
							 * En de que no exista where se lanza un aviso al usuario. 
							 */
							if(contadorquery==splited.length) {
								String input="";
								System.out.print("WARNING: THERE'S NO WHERE!!!\nARE U SURE THAT U WANT TO CONTINUE? (Y/N)");
								input = scanner.nextLine();
								if(input.equals("N")) break;
							}
							if(contadorquery==0) {
								throw new Exception("Invalid query, there's no fields to set");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					/*
					 * A partir de la segunda iteracion la flag se vuelve true y todos los valores se usaran para los values.
					 */
					else {
						/*
						 * El parametro querycounter vale para marcar cual es el ultimo valor del set
						 */
						int querycounter = 0;
						querycounter = contadorquery-1;
						int counter= 0;
						for(String str: splited) {
							/*
							 * Este if marca el final de la linea
							 */
							if(counter==splited.length-1) {
								if(where.contains(counter)) {
									querywhere += campos[counter]+" = '"+str+"';\n ";
									counter++;
								}
								/*
								 * En caso de no existir el where no entraria en el if de arriba. No es que vaya a pasar. Pero por si alguien quiere joderla.
								 */
								else {
									query += campos[counter]+" = '"+str+"';\n ";
									counter++;
								}
							}
							else {
								/*
								 * En este if se comprueva los valores que han de ir al where.
								 */
									if(where.contains(counter)) {
										querywhere += campos[counter]+" = '"+str+"', ";
										counter++;
									}
									else {
										/*
										 * En este if se comprueva si es el ultimo campo del set o no;
										 */
										if(querycounter==0) {
											query += campos[counter]+" = '"+str+"'";
											counter++;
										}else {
											query += campos[counter]+" = '"+str+"', ";
											counter++;
											querycounter--;
										}
									}
							}
						}
						/*
						 * En caso de no existir el where no se metera la string querywhere ya que no es necesario.
						 */
						if(contadorquery == splited.length) output+= query;
						else output+= query + querywhere;
					}
				}
			}
			/*
			 * Se crea el nuevo archivo con nombre en base a la opcion y el nombre de la tabla.
			 * Luego de escribe la consulta en el archivo  y se cierran todos los readers y writers
			 */
			File f = new File("./"+filename);
			FileWriter fw = new FileWriter(f);
			fw.write(output);
			reader.close();
			csvreader.close();
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	
	
	/*Funciona 
	public static void test() throws IOException {
		//Estructura basica para leer el arxivo csv
		try {
			Reader reader = Files.newBufferedReader(Paths.get(path_string));
			CSVReader csvreader = new CSVReader(reader);
			String[] array;
			while((array = csvreader.readNext()) != null) {
				for(String s : array) {
				}
			}
			reader.close();
			csvreader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}*/
}
