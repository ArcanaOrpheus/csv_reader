package csv_to_sql;

public class reader {
	
	public static void main(String[] args) {
		
		String option = args.toString();
		
		
		if(option.equals("-i")) insert();
		if(option.equals("-u")) update();
		if(option.equals("-d")) delete();
		
	}
	
}
