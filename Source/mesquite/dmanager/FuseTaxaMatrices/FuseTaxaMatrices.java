/* Mesquite source code.  Copyright 1997-2005 W. Maddison and D. Maddison.  Version 1.06, September 2005. Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code.  The commenting leaves much to be desired. Please approach this source code with the spirit of helping out. Perhaps with your help we can be more than a few, and make Mesquite better.  Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY. Mesquite's web site is http://mesquiteproject.org  This source code and its compiled class files are free and modifiable under the terms of  GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.dmanager.FuseTaxaMatrices;/*~~  */import java.util.*;import java.awt.*;import java.awt.image.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;/* ======================================================================== */public class FuseTaxaMatrices extends FileAssistantT {	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		includeFuse(commandRec);		return true;	}		/*.................................................................................................................*/	public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {		if (checker.compare(this.getClass(), "Includes a file and fuses taxa/characters block", null, commandName, "fuse")) {			includeFuse(commandRec);		}		else			return super.doCommand(commandName, arguments, commandRec, checker);		return null;	}		/*.................................................................................................................*/		private void includeFuse(CommandRecord commandRec){		String message = "You are about to read in another file, and fuse the taxa and characters blocks found there with taxa and character blocks in ";		message += "the current file.  This fusion process will NOT incorporate footnotes and other auxiliary information associated ";		message += "with those taxa and character blocks.";				discreetAlert(commandRec, message);		MesquiteModule fCoord = getFileCoordinator();		fCoord.doCommand("includeFile", StringUtil.argumentMarker + "fuseTaxaCharBlocks", commandRec, CommandChecker.defaultChecker);	}	/*.................................................................................................................*/	public boolean isPrerelease() { 		return false;	}		/*.................................................................................................................*/	public String getNameForMenuItem() {		return "Fuse Taxa and Matrix from File...";	}	/*.................................................................................................................*/	public String getName() {		return "Include-Fuse Taxa and Matrix";	}	/*.................................................................................................................*/ 	/** returns the version number at which this module was first released.  If 0, then no version number is claimed.  If a POSITIVE integer 	 * then the number refers to the Mesquite version.  This should be used only by modules part of the core release of Mesquite. 	 * If a NEGATIVE integer, then the number refers to the local version of the package, e.g. a third party package*/    	public int getVersionOfFirstRelease(){    		return 107;      	}	/*.................................................................................................................*/	/** returns an explanation of what the module does.*/	public String getExplanation() {		return "Includes a file and fuses its taxa and character blocks, for instance to add seqences." ;  //Debugg.println	}	}