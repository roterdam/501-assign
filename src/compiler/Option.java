package compiler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Option {
	
	public static String DART_SDK;
	public static String START;
	public static String CONFIG_FILE = "config";
	
	static {
		try {
			BufferedReader reader = new BufferedReader( new InputStreamReader(new FileInputStream( CONFIG_FILE ) ) );
			String lineStr;
			while ( (lineStr = reader.readLine()) != null ) {
				if ( lineStr.startsWith( "#" ) )
					continue;
				else if ( lineStr.startsWith( "DART" ) ) {
					DART_SDK = lineStr.substring( lineStr.indexOf( '=' ) + 1 ) + "/bin/dart"; 
				} else if ( lineStr.startsWith( "START" ))
					START = lineStr.substring( lineStr.indexOf( '=' ) + 1 ) + "/bin/start.dart";
			}
		} catch ( FileNotFoundException e ) {
			e.printStackTrace();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
	
	public enum BackendOption {
		ASM,
		CFG,
		IR,
		SSA,
		Report,
	};
	
	public enum OptimizeOption {
		CP, // 
		VN,
		SSA,
	};
	
	public enum ProfileOption {
		POS,
		INLINE,
	};
	
	public List<String> options;
	public String fileName;
	public List<OptimizeOption> optimizeList;
	public List<ProfileOption> profileList;
	public BackendOption backend;
	
	public void usage() {
		System.out.println("java -jar compiler.jar <filename> [-opt=<optimize>] [-backend=<backend>] [-profile=<profile>]\n");
		System.out.println("Optimization supported options:");
		System.out.println("ssa\tSSA optimization");
		System.out.println("cp\tConstant propagation optimization (depends on SSA)");
		System.out.println("vn\tValue numbering optimization (depends on SSA)");
		System.out.println("\nProfile supported options:");
		System.out.println("pos\tBasic block positioning to optimize branch prediction and icache");
		System.out.println("inline\tInline methods");
		System.out.println("\nBackend supported options:");
		System.out.println("asm\tAssembly code (default)");
		System.out.println("cfg\tControl flow graph");
		System.out.println("ir\tIntermediate representation");
		System.out.println("ssa\tSSA code");
		System.out.println("report\tReport");
		
	}
	
	public boolean parse(String[] args) {
		
		options = new LinkedList<String>();
		optimizeList = new LinkedList<OptimizeOption>();
		profileList = new LinkedList<ProfileOption>();
		backend = BackendOption.IR;
		
		for (int i = 0; i < args.length; i++)
			options.add(args[i]);
		
		Iterator<String> it = options.iterator();
		if (it.hasNext()) {
			fileName = it.next();
//			System.out.println(fileName);
		} else
			return false;
		
		while (it.hasNext()) {
			String arg = it.next();
			
			while (arg.startsWith("-"))
				arg = arg.substring(1);
			
			if (arg.startsWith("opt")) {
				
				arg = arg.substring(arg.indexOf('=') + 1).toLowerCase();
				String[] term = arg.split(",");
				for (String s: term) {
					if (s.equals("cp"))
						optimizeList.add(OptimizeOption.CP);
					else if (s.equals("vn"))
						optimizeList.add(OptimizeOption.VN);
					else if (s.equals("ssa"))
						optimizeList.add(OptimizeOption.SSA);
					else {
						System.out.println("Unsupported optimization option: " + s + "\n");
						return false;
					}
				}
				
			} else if (arg.startsWith("backend")) {
				arg = arg.substring(arg.indexOf('=') + 1).toLowerCase();
				if (arg.equals("asm"))
					backend = BackendOption.ASM;
				else if (arg.equals("ir"))
					backend = BackendOption.IR;
				else if (arg.equals("cfg"))
					backend = BackendOption.CFG;
				else if (arg.equals("ssa"))
					backend = BackendOption.SSA;
				else if (arg.equals("report"))
					backend = BackendOption.Report;
				else {
					System.out.println("Unsupported backend option: " + arg + "\n");
					return false;
				}
			} else if (arg.startsWith("profile")) {
				arg = arg.substring(arg.indexOf('=') + 1).toLowerCase();
				String[] term = arg.split(",");
				for (String s: term) {
					if (s.equals("pos"))
						profileList.add(ProfileOption.POS);
					else if (s.equals("inline"))
						profileList.add(ProfileOption.INLINE);
					else {
						System.out.println("Unsupported profile option: " + s + "\n");
						return false;
					}
				}
			}
		}
		
		return true;
	}
}