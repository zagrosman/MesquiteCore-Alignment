/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.charMatrices.RandomFillCateg;/*~~  */import java.util.*;import java.lang.*;import java.awt.*;import java.awt.image.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.categ.lib.*;import mesquite.lib.table.*;/* ======================================================================== */public class RandomFillCateg extends CategDataAlterer {	CharacterState fillState;	int maxState = 1;	Random rng;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		rng = new Random();		rng.setSeed(System.currentTimeMillis());		return true;	}	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */   	public boolean requestPrimaryChoice(){   		return true;     	}	/*.................................................................................................................*/   	public void alterCell(CharacterData data, int ic, int it, CommandRecord commandRec){   		((CategoricalData)data).setState(ic,it, randomState());   	}	/*.................................................................................................................*/   	/** Called to alter data in those cells selected in table*/   	public boolean alterData(CharacterData data, MesquiteTable table, CommandRecord commandRec){						if (!(data instanceof CategoricalData))				return false;   			if (data instanceof DNAData)				maxState = 3;   			else if (data instanceof ProteinData)				maxState = 19;			else {				maxState = MesquiteInteger.queryInteger(containerOfModule(), "Maximum State", "Each state is chosen equiprobably when filling the matrix randomly.  What should be the maximum state value?", 1, 1, 50, true);				if (!MesquiteInteger.isCombinable(maxState))					return false;			} 			return alterContentOfCells(data,table, commandRec);  	}	//	Double d = new Double(value);   		/*.................................................................................................................*/   	long randomState(){		double value = rng.nextDouble() * (maxState+1);		int e = (int)value;		if ((e>=0)&&(e<=CategoricalState.maxCategoricalState))			return CategoricalState.makeSet(e);		else {			return CategoricalState.makeSet(0);		}   	}	/*.................................................................................................................*/  	 public boolean showCitation() {		return true;   	 }	/*.................................................................................................................*/   	 public boolean isPrerelease(){   	 	return false;    	 }	/*.................................................................................................................*/    	 public String getName() {		return "Random Fill (Categorical)";   	 }	/*.................................................................................................................*/ 	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Fills cells with a randomly-chosen state. For DNA data, states A, C, G, and T are chosen with equal probability; for other data, states up to and including the maximum state value are chosen with equal probability." ;   	 }   	 }