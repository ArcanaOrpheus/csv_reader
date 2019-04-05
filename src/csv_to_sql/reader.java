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
	
	public static void main(String[] args) throws Exception {
		/*
		 * Clase que convierte csv separados por ";" en scripts sql para automatizar el trabajo con los clientes
		 * La clase recive por comando la opcion y el nombre de la tabla.
		 * El nombre del script sera siempre (opcion)_(nombre tabla).sql
		 * Funcion para testear cosas.
		 * Porfavor meter las cosas nuevas o que no funcionen en esta clase o usar debugger.
		 *	test();
		 * 3 Funciones principales con nombres autodescriptivos
		 */
		String path = "";
		boolean pathcheck=false;
		String option = "";
		boolean optioncheck=false;
		String tablename="";
		boolean tablecheck=false;
		if(args.length==0) {
			pathcheck = optioncheck = tablecheck = true;
			System.out.println("There are no arguments for the method, starting manual input");
			Scanner scanner = new Scanner(System.in);
			System.out.println("Introduce path to file: ");
			path= scanner.nextLine();
			System.out.println("Introduce option: ");
			option = scanner.nextLine();
			System.out.print("Introduce table name: ");
			tablename = scanner.nextLine();
			scanner.close();
		}
		else {
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
				if( (args[i].equals("-u")) || (args[i].equals("-i")) || (args[i].equals("-d")) ) {
					optioncheck=true;
				}
			}
			if(!pathcheck) {
				pathcheck=true;
				Scanner scanner = new Scanner(System.in);
				System.out.println("Introduce path to file: ");
				path= scanner.next();
				scanner.close();
			}
			if(!optioncheck) {
				optioncheck=true;
				Scanner scanner = new Scanner(System.in);
				System.out.println("Introduce option: ");
				option = scanner.next();
				scanner.close();
			}
			if(!tablecheck) {
				Scanner scanner = new Scanner(System.in);
				System.out.print("Introduce table name: ");
				tablename = scanner.next();
				scanner.close();
			}
		}
		if(option.equals("-i")) insert(tablename, path);
		if(option.equals("-u")) update(tablename, path);
		if(option.equals("-d")) delete(tablename, path);
	}
	
	public static void insert(String tablename, String path) throws IOException {
		String filename = "insert_"+tablename+".sql";
		String query = "INSERT into "+tablename+" (";
		try {
			Reader reader = Files.newBufferedReader(Paths.get(path));
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
		String filename = "update_"+tablename+".sql";
		try {
			Reader reader = Files.newBufferedReader(Paths.get(path));
			CSVReader csvreader = new CSVReader(reader);
			String[] array;
			String[] campos = null;
			List <Integer> where  = new ArrayList<Integer>();
			boolean flag=false;
			String output ="";
			int contadorquery = 0;
			while((array = csvreader.readNext()) != null) {
				String query = "UPDATE "+tablename+" set ";
				String querywhere = " where ";
				for(String s : array) {
					String[] splited = s.split(";");
					if(!flag) {
						try {
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
							if(contadorquery==0) {
								throw new Exception("Invalid query, there's no fields to set");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					else {
						int querycounter = 0;
						querycounter = contadorquery-1;
						int counter= 0;
						for(String str: splited) {
							if(counter==splited.length-1) {
								if(where.contains(counter)) {
									querywhere += campos[counter]+" = '"+str+"';\n ";
									counter++;
								}
								else {
									query += campos[counter]+" = '"+str+"';\n ";
									counter++;
								}
							}
							else {
									if(where.contains(counter)) {
										querywhere += campos[counter]+" = '"+str+"', ";
										counter++;
									}
									else {
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
						output+= query + querywhere;
					}
				}
			}
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
