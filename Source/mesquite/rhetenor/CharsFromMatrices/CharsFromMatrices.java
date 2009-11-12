/* Mesquite source code (Rhetenor package).  Copyright 1997-2009 E. Dyreson and W. Maddison. Version 2.71, September 2009.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.rhetenor.CharsFromMatrices;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.rhetenor.lib.*;/* ======================================================================== *//* This is a character source used privately by Rhetenor for ShowCharLoadings.  Note that it is not user chooseable. */public class CharsFromMatrices extends CharsFromMatrixSource {	public void getEmployeeNeeds(){  //This gets called on startup to harvest information; override this and inside, call registerEmployeeNeed		EmployeeNeed e = registerEmployeeNeed(MatrixSourceCoord.class, getName() + "  needs a source of matrices.",		"The source of matrices is selected initially");	}	/*.................................................................................................................*/	int currentChar=0;	MatrixSourceCoord matrixSource;	MCharactersDistribution matrix;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) {  	 	return true;   	 }	/*.................................................................................................................*/	public boolean startJob(Object condition) {  	 	return false;   	 }  	 	/*.................................................................................................................*/	/** Generated by an employee who quit.  The MesquiteModule should act accordingly. */ 	public void employeeQuit(MesquiteModule employee) { 		if (employee == matrixSource)  // character source quit and none rehired automatically 			iQuit();	}   	public boolean getUserChooseable(){   		return false;   	}   	/** Called to provoke any necessary initialization.  This helps prevent the module's intialization queries to the user from   	happening at inopportune times (e.g., while a long chart calculation is in mid-progress)*/   	public void initialize(Taxa taxa){   	}	/*.................................................................................................................*/ 	public void setMatrixSource(MatrixSourceCoord source){ 		matrixSource = source; 	}	/*.................................................................................................................*/   	public CharacterDistribution getCharacter(Taxa taxa, int ic) {   		currentChar =ic;		matrix = matrixSource.getCurrentMatrix(taxa); //TODO: don't do this every time!		if (matrix == null)			return null;  		CharacterDistribution dist =  matrix.getCharacterDistribution(ic);  		if (dist instanceof CharacterStates)  			((CharacterStates)dist).setParentCharacter(ic);  		return dist;   	}	/*.................................................................................................................*/   	public int getNumberOfCharacters(Taxa taxa) {   		if (matrix==null) {   			if (matrixSource==null)   				return 0;			matrix = matrixSource.getCurrentMatrix(taxa); //TODO: don't do this every time!   		}   		return matrix.getNumChars();   	}   	public boolean usesTree(){   		if (matrixSource==null)   			return false;   		else   			return matrixSource.usesTree();   	}	/*.................................................................................................................*/   	public CharacterDistribution getCharacter(Tree tree, int ic) {   		currentChar =ic;		matrix = matrixSource.getCurrentMatrix(tree); //TODO: don't do this every time!  		CharacterDistribution dist =  matrix.getCharacterDistribution(ic);  		if (dist instanceof CharacterStates)  			((CharacterStates)dist).setParentCharacter(ic);  		return dist;   	}	/*.................................................................................................................*/   	public int getNumberOfCharacters(Tree tree) {   		if (matrix==null) {   			if (matrixSource==null)   				return 0;			matrix = matrixSource.getCurrentMatrix(tree); //TODO: don't do this every time!   		}   		return matrix.getNumChars();   	}      	/** returns the name of character ic*/   	public String getCharacterName(Taxa taxa, int ic){   		return "Character #" + CharacterStates.toExternal(ic) ;   	}	/*.................................................................................................................*/  	 public CompatibilityTest getCompatibilityTest() {  	 	return new CharacterStateTest();  	 }	/*.................................................................................................................*/    	 public String getName() {		return "Characters from Matrix Source";   	 }	/*.................................................................................................................*/ 	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Supplies characters from source of matrices." ;   	 }	/*.................................................................................................................*/    	 public boolean isPrerelease() {		return false;   	 }   	 }