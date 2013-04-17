import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class Parser {
	private List<Stmt> stmts;
	private List<Routine> routines;
	
	public Parser() {
		stmts = new LinkedList<Stmt>();
		routines = new LinkedList<Routine>();
	}
	
	public void scanFile(String filename) {
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("method")) {
					routines.add(Routine.parseRoutine(line));
				} else if (line.startsWith("instr")) {
					stmts.add(Stmt.parseStmt(line));
				}
				//System.out.println(line);
			}
			reader.close();
			
		} catch (FileNotFoundException e) {
			System.out.println(filename + "doesn't exist");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		{
			int beginLine = routines.get(0).getStartLine();
			int endLine;
			Routine routine;
			for (int i = 0; i < routines.size() - 1; i++) {
				endLine = routines.get(i + 1).getStartLine() - 1;
				routine = routines.get(i);
				routine.setEndLine(endLine);
				routine.setStmts(stmts.subList(beginLine - 1, endLine));
				beginLine = endLine + 1;
			}
			endLine = stmts.size() - 1; // last instr: nop, skip this one
			routine = routines.get(routines.size() - 1);
			routine.setEndLine(endLine);
			routine.setStmts(stmts.subList(beginLine - 1, endLine));
			
			/*for (int i = 0; i < routines.size(); i++) {
				routines.get(i).dump();
			}*/
		}
	}
	
	public void parseRoutine() {
		for (Routine r: routines) {
			r.parse();
		}
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("CFG.jar [Input file]"	);
			//for (String arg: args)
			//	System.out.println(arg);
			return;
		}
		Parser p = new Parser();
		p.scanFile(args[0]);
		
		p.parseRoutine();
	}
}