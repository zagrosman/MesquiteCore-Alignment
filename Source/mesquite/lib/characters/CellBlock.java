package mesquite.lib.characters;

/* Mesquite source code.  Copyright 1997-2007 W. Maddison and D. Maddison.
Version 2.01, December 2007.
Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. 
The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.
Perhaps with your help we can be more than a few, and make Mesquite better.

Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.
Mesquite's web site is http://mesquiteproject.org

This source code and its compiled class files are free and modifiable under the terms of 
GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)
 */

import mesquite.lib.*;
import mesquite.lib.table.*;
import mesquite.categ.lib.*;

/* ======================================================================== */
public class CellBlock {
	CategoricalData data;
	MesquiteTable table;

	MesquiteInteger firstCharInBlock;
	MesquiteInteger lastCharInBlock;
	int originalFirstCharInFullBlock;
	int originalLastCharInFullBlock;
	int originalFirstCharInBlock;
	int originalLastCharInBlock;
	int currentFirstCharInBlock = 0;
	int currentLastCharInBlock = 0;
	int previousFirstCharInBlock = 0;
	int previousLastCharInBlock = 0;

	MesquiteInteger firstTaxonInBlock;
	MesquiteInteger lastTaxonInBlock;
	int originalFirstTaxonInFullBlock;
	int originalLastTaxonInFullBlock;
	int originalFirstTaxonInBlock;
	int originalLastTaxonInBlock;
	int currentFirstTaxonInBlock = 0;
	int currentLastTaxonInBlock = 0;
	int previousFirstTaxonInBlock = 0;
	int previousLastTaxonInBlock = 0;

	int maxLeftMovement=0;
	int maxRightMovement=0;

	int currentLeftMovement = 0;
	int currentRightMovement = 0;

	boolean atEdgeLeft = false;
	boolean atEdgeRight = false;
	boolean isRight = false;
	boolean isLeft = false;
	
	boolean locked = false;

	public CellBlock(CategoricalData data, MesquiteTable table) {
		this.data = data;
		this.table = table;
	}
	/*.................................................................................................................*/
	public void reset(){    
		currentLeftMovement = 0;
		currentRightMovement = 0;
		setAllBlocks(originalFirstCharInBlock, originalLastCharInBlock,originalFirstTaxonInBlock,originalLastTaxonInBlock);
		locked = false;
	}
	/*.................................................................................................................*/
	public void restoreCharBlock(MesquiteBoolean dataChanged){    // takes data that is currently at currentBlock location and move to original location
		if (!(currentLeftMovement==0 && currentRightMovement==0)) {
			int distanceToMove = originalFirstCharInBlock - previousFirstCharInBlock;
			int added = data.moveCells(previousFirstCharInBlock,previousLastCharInBlock, distanceToMove, currentFirstTaxonInBlock, currentLastTaxonInBlock, true, false, true, false,dataChanged);

			table.redrawBlock(MesquiteInteger.minimum(previousFirstCharInBlock, originalFirstCharInBlock), currentFirstTaxonInBlock, MesquiteInteger.maximum(previousLastCharInBlock, originalLastCharInBlock), currentLastTaxonInBlock);
			reset();
		}
	}
	/*.................................................................................................................*/


	/*.................................................................................................................*/
	public void setMaximumMovements (int maxLeftMovement, int maxRightMovement){
		this.maxLeftMovement = maxLeftMovement;
		this.maxRightMovement = maxRightMovement;
	}
	/*.................................................................................................................*/
	public void setMaximumMovements (){
		this.maxLeftMovement = gapColumnsToLeftOfBlock();
		this.maxRightMovement = gapColumnsToRightOfBlock();
	}
	public int getMaxLeftMovement (){
		return  maxLeftMovement;
	}
	public int getMaxRightMovement (){
		return maxRightMovement;
	}
	/*.................................................................................................................*/
	public int availableLeftMovement (){
		return maxLeftMovement-currentLeftMovement;
	}
	/*.................................................................................................................*/
	public int availableRightMovement (){
		return maxRightMovement-currentRightMovement;
	}
	/*.................................................................................................................*/
	public int movementAllowed (int candidateMovement, boolean canExpand){
		if (candidateMovement<0){  // move to left
			if (canExpand & isLeft & atEdgeLeft)   // any amount can be accommodated
				return candidateMovement;
			if ((-candidateMovement)<=availableLeftMovement())  // it is acceptable
				return candidateMovement;
			return availableLeftMovement();
		}
		if (candidateMovement>0){  // move to right
			if (canExpand & isRight & atEdgeRight)
				return candidateMovement;
			if (candidateMovement<=availableRightMovement())  // it is acceptable
				return candidateMovement;
			return availableRightMovement();
		}
		return 0;
	}
	/*.................................................................................................................*/
	public void switchCharBlock(int icStart, int icEnd){  
		originalFirstCharInBlock=icStart;
		originalLastCharInBlock=icEnd;
		currentFirstCharInBlock=icStart;
		currentLastCharInBlock=icEnd;
		previousFirstCharInBlock=icStart;
		previousLastCharInBlock=icEnd;
	}
	/*.................................................................................................................*/
	public void setAllBlocks(int icStart, int icEnd, int itStart, int itEnd){  
		originalFirstCharInBlock=icStart;
		originalLastCharInBlock=icEnd;
		originalFirstTaxonInBlock=itStart;
		originalLastTaxonInBlock=itEnd;
		currentFirstCharInBlock=icStart;
		currentLastCharInBlock=icEnd;
		currentFirstTaxonInBlock=itStart;
		currentLastTaxonInBlock=itEnd;
		previousFirstCharInBlock=icStart;
		previousLastCharInBlock=icEnd;
		previousFirstTaxonInBlock=itStart;
		previousLastTaxonInBlock=itEnd;
	}
	/*.................................................................................................................*/
	public void setOriginalFullBlockOnTouch(int icStart, int icEnd, int itStart, int itEnd){  
		originalFirstCharInFullBlock=icStart;
		originalLastCharInFullBlock=icEnd;
		originalFirstTaxonInFullBlock=itStart;
		originalLastTaxonInFullBlock=itEnd;
	}
	/*.................................................................................................................*/
	public void setOriginalBlock(int icStart, int icEnd, int itStart, int itEnd){  
		originalFirstCharInBlock=icStart;
		originalLastCharInBlock=icEnd;
		originalFirstTaxonInBlock=itStart;
		originalLastTaxonInBlock=itEnd;
	}
	/*.................................................................................................................*/
	public void setOriginalFirstCharInBlock(int icStart){  
		originalFirstCharInBlock=icStart;
	}
	/*.................................................................................................................*/
	public int getOriginalFirstCharInBlock(){  
		return originalFirstCharInBlock;
	}
	/*.................................................................................................................*/
	public int getOriginalLastCharInBlock(){  
		return originalLastCharInBlock;
	}
	/*.................................................................................................................*/
	public int getOriginalFirstTaxonInBlock(){  
		return originalFirstTaxonInBlock;
	}
	/*.................................................................................................................*/
	public int getOriginalLastTaxonInBlock(){  
		return originalLastTaxonInBlock;
	}
	/*.................................................................................................................*/
	public int getOriginalFirstCharInFullBlock(){  
		return originalFirstCharInFullBlock;
	}
	/*.................................................................................................................*/
	public int getOriginalLastCharInFullBlock(){  
		return originalLastCharInFullBlock;
	}
	/*.................................................................................................................*/
	public int getOriginalFirstTaxonInFullBlock(){  
		return originalFirstTaxonInFullBlock;
	}
	/*.................................................................................................................*/
	public int getOriginalLastTaxonInFullBlock(){  
		return originalLastTaxonInFullBlock;
	}
	/*.................................................................................................................*/
	public void setCurrentCharBlock(int icStart, int icEnd){  
		currentFirstCharInBlock=icStart;
		currentLastCharInBlock=icEnd;
	}
	/*.................................................................................................................*/
	public void setCurrentBlock(int icStart, int icEnd, int itStart, int itEnd){  
		currentFirstCharInBlock=icStart;
		currentLastCharInBlock=icEnd;
		currentFirstTaxonInBlock=itStart;
		currentLastTaxonInBlock=itEnd;
	}
	/*.................................................................................................................*/
	public void shiftCurrentBlock(int shift){  
		currentFirstCharInBlock+=shift;
		currentLastCharInBlock+=shift;
		//currentFirstTaxonInBlock+=shift;
		//currentLastTaxonInBlock+=shift;
	}
	/*.................................................................................................................*/
	public int getCurrentFirstCharInBlock(){  
		return currentFirstCharInBlock;
	}
	/*.................................................................................................................*/
	public int getCurrentLastCharInBlock(){  
		return currentLastCharInBlock;
	}
	/*.................................................................................................................*/
	public int getCurrentFirstTaxonInBlock(){  
		return currentFirstTaxonInBlock;
	}
	/*.................................................................................................................*/
	public int getCurrentLastTaxonInBlock(){  
		return currentLastTaxonInBlock;
	}
	/*.................................................................................................................*/
	public void setPreviousCharBlock(int icStart, int icEnd){  
		previousFirstCharInBlock=icStart;
		previousLastCharInBlock=icEnd;
	}
	/*.................................................................................................................*/
	public void addToCharBlockValues(int added){  
		originalFirstCharInBlock+=added;
		originalLastCharInBlock+=added;
		currentFirstCharInBlock+=added;
		currentLastCharInBlock+=added;
		previousFirstCharInBlock+=added;
		previousLastCharInBlock+=added;
	}

	/*.................................................................................................................*/
	public void transferCurrentToPrevious(){  
		previousFirstCharInBlock=currentFirstCharInBlock;
		previousLastCharInBlock=currentLastCharInBlock;
		previousFirstTaxonInBlock=currentFirstTaxonInBlock;
		previousLastTaxonInBlock=currentLastTaxonInBlock;
	}
	/*.................................................................................................................*/
	public void setPreviousBlock(int icStart, int icEnd, int itStart, int itEnd){  
		previousFirstCharInBlock=icStart;
		previousLastCharInBlock=icEnd;
		previousFirstTaxonInBlock=itStart;
		previousLastTaxonInBlock=itEnd;
	}
	/*.................................................................................................................*/
	public int getPreviousFirstCharInBlock(){  
		return previousFirstCharInBlock;
	}
	/*.................................................................................................................*/
	public int getPreviousLastCharInBlock(){  
		return previousLastCharInBlock;
	}
	/*.................................................................................................................*/
	public int getPreviousFirstTaxonInBlock(){  
		return previousFirstTaxonInBlock;
	}
	/*.................................................................................................................*/
	public int getPreviousLastTaxonInBlock(){  
		return previousLastTaxonInBlock;
	}
	public void adjustToMove(int movement) {
		if (movement<0){   //moving left
			currentLeftMovement-=movement;
			currentRightMovement+=movement;
		}
		else if (movement>0){   //moving right
			currentLeftMovement-=movement;
			currentRightMovement+=movement;
		}
		shiftCurrentBlock(movement);

	}
	/*.................................................................................................................*/
	public void addCharacters(int added, boolean toStart){  
		if (toStart) {
			originalFirstCharInBlock+=added;
			originalLastCharInBlock+=added;
			currentFirstCharInBlock+=added;
			currentLastCharInBlock+=added;
			previousFirstCharInBlock+=added;
			previousLastCharInBlock+=added;
		}
		if (isLeft && toStart)
			maxLeftMovement+=added;
		if (isRight && !toStart)
			maxRightMovement+=added;
	}
	/*.................................................................................................................*/
	public int gapColumnsToLeftOfBlock() {
		atEdgeLeft = false;
		if (currentFirstCharInBlock<=0) {
			atEdgeLeft = true;
			return 0;
		}
		int count = 0;
		for (int ic = currentFirstCharInBlock-1; ic>=0; ic--) {
			if (data.inapplicableBlock(ic, ic, currentFirstTaxonInBlock, currentLastTaxonInBlock)) {
				count++;
				if (ic==0)
					atEdgeLeft=true;
			}
			else
				break;
		}
		return count;
	}
	/*.................................................................................................................*/
	public int gapColumnsToRightOfBlock() {
		atEdgeRight=false;
		if (currentLastCharInBlock>=data.getNumChars()-1) {
			atEdgeRight=true;
			return 0;
		}
		int count = 0;
		for (int ic = currentLastCharInBlock+1; ic<data.getNumChars(); ic++) {
			if (data.inapplicableBlock(ic, ic, currentFirstTaxonInBlock, currentLastTaxonInBlock)){
				count++;
				if (ic==data.getNumChars()-1)
					atEdgeRight=true;
			}
			else
				break;
		}
		return count;
	}
	/*.................................................................................................................*/
	public void getBlockInSequence(int ic, int it, MesquiteInteger firstInBlock, MesquiteInteger lastInBlock, boolean wholeSelectedBlock, boolean wholeSequenceLeft, boolean wholeSequenceRight, MesquiteBoolean cellHasInapplicable, MesquiteBoolean leftIsInapplicable, MesquiteBoolean rightIsInapplicable){  // determines the block that was touched
		cellHasInapplicable.setValue(false);
		leftIsInapplicable.setValue(false);
		rightIsInapplicable.setValue(false);
		firstInBlock.setValue(0);
		lastInBlock.setValue(data.getNumChars());
		if (ic>0)
			if (data.isInapplicable(ic-1, it))
				leftIsInapplicable.setValue(true);
		if (ic<data.getNumChars())
			if (data.isInapplicable(ic+1, it))
				rightIsInapplicable.setValue(true);
/*
 * 		if (data.isInapplicable(ic, it)) {
			firstInBlock.setValue(ic);
			lastInBlock.setValue(-1);
			cellHasInapplicable.setValue(true);
			return;
		}
		*/
		if (wholeSequenceLeft) {
			firstInBlock.setValue(data.firstApplicable(it));
		} 
		else if (wholeSelectedBlock) {
			for (int i=ic; i>=0; i--) {   // find first unselected cell to the left of this point
				if (!table.isCellSelected(i, it)){  // should be isToolInapplicable
					firstInBlock.setValue(i+1);
					break;
				}
			}
		}
		else {
			for (int i=ic; i>=0; i--) {   // find first gap to the left of this point
				if (data.isInapplicable(i, it)){  // should be isToolInapplicable
					firstInBlock.setValue(i+1);
					break;
				}
			}
		}


		if (wholeSequenceRight) {
			lastInBlock.setValue(data.lastApplicable(it));
		}
		else if (wholeSelectedBlock) {
			for (int i=ic; i<data.getNumChars(); i++) {  // find first unselected cell to the right of this point
				if (!table.isCellSelected(i, it)){ 
					lastInBlock.setValue(i-1);
					return;
				}
			}
		}
		else {
			for (int i=ic; i<data.getNumChars(); i++) {  // find first gap to the right of this point
				if (data.isInapplicable(i, it)){  // should be isToolInapplicable
					lastInBlock.setValue(i-1);
					return;
				}
			}
		}
	}
	/** Gets the cell block that contains the cells of character ic from itStart to itEnd */
	/*.................................................................................................................*/
	public void getCellBlock(int icStart, int icEnd, int itStart, int itEnd, MesquiteInteger firstInBlock, MesquiteInteger lastInBlock, boolean wholeSelectedBlock, boolean wholeSequenceLeft, boolean wholeSequenceRight, MesquiteBoolean cellHasInapplicable, MesquiteBoolean leftIsInapplicable, MesquiteBoolean rightIsInapplicable){  // determines the block that was touched
		cellHasInapplicable.setValue(false);
		leftIsInapplicable.setValue(false);
		rightIsInapplicable.setValue(false);
		firstInBlock.setValue(0);
		lastInBlock.setValue(data.getNumChars());
		if (icStart>0)
			if (data.inapplicableBlock(icStart-1, icStart-1, itStart, itEnd))
					leftIsInapplicable.setValue(true);
		if (icEnd<data.getNumChars())
			if (data.inapplicableBlock(icEnd+1, icEnd+1, itStart, itEnd))
				rightIsInapplicable.setValue(true);
/*
 * 		if (data.isInapplicable(ic, it)) {
			firstInBlock.setValue(ic);
			lastInBlock.setValue(-1);
			cellHasInapplicable.setValue(true);
			return;
		}
		*/
		if (wholeSequenceLeft) {
			firstInBlock.setValue(data.firstApplicable(itStart, itEnd));
		} 
		else if (wholeSelectedBlock) {
			for (int i=icStart; i>=0; i--) {   // find first unselected cell to the left of this point
				if (!table.isAnyCellSelectedInBlock(i, i, itStart, itEnd)){ 
					firstInBlock.setValue(i+1);
					break;
				}
			}
		}
		else {
			for (int i=icStart; i>=0; i--) {   // find first gap to the left of this point
				if (data.inapplicableBlock(i, i, itStart,itEnd)){  // should be isToolInapplicable
					firstInBlock.setValue(i+1);
					break;
				} else if (i<icStart&&!data.applicableInBothCharacters(i,i+1,itStart,itEnd)) {
					firstInBlock.setValue(i+1);
					break;
				}
			}
		}


		if (wholeSequenceRight) {
			lastInBlock.setValue(data.lastApplicable(itStart, itEnd));
		}
		else if (wholeSelectedBlock) {
			for (int i=icEnd; i<data.getNumChars(); i++) {  // find first unselected cell to the right of this point
				if (!table.isAnyCellSelectedInBlock(i, i, itStart, itEnd)){ 
					lastInBlock.setValue(i-1);
					return;
				}
			}
		}
		else {
			for (int i=icEnd; i<data.getNumChars(); i++) {  // find first gap to the right of this point
				if (data.inapplicableBlock(i,i, itStart, itEnd)){  // should be isToolInapplicable
					lastInBlock.setValue(i-1);
					return;
				} else if (i>icEnd &&!data.applicableInBothCharacters(i,i-1,itStart,itEnd)) {
					lastInBlock.setValue(i-1);
					return;
				}
			}
		}
	}
	
	/* ............................................................................................................... */
	/** Select block of cells. */
	public void deselectOthersAndSelectBlock() {
			table.deSelectAndRedrawOutsideBlock(currentFirstCharInBlock, currentFirstTaxonInBlock, currentLastCharInBlock, currentLastTaxonInBlock);
			table.selectBlock(currentFirstCharInBlock, currentFirstTaxonInBlock, currentLastCharInBlock, currentLastTaxonInBlock);
		
	}

	public int getCurrentLeftMovement() {
		return currentLeftMovement;
	}
	public void setCurrentLeftMovement(int currentLeftMovement) {
		this.currentLeftMovement = currentLeftMovement;
	}
	public int getCurrentRightMovement() {
		return currentRightMovement;
	}
	public void setCurrentRightMovement(int currentRightMovement) {
		this.currentRightMovement = currentRightMovement;
	}
	public boolean isAtEdgeLeft() {
		return atEdgeLeft;
	}
	public void setAtEdgeLeft(boolean atEdgeLeft) {
		this.atEdgeLeft = atEdgeLeft;
	}
	public boolean isAtEdgeRight() {
		return atEdgeRight;
	}
	public void setAtEdgeRight(boolean atEdgeRight) {
		this.atEdgeRight = atEdgeRight;
	}
	public boolean getLeft() {
		return isLeft;
	}
	public boolean getRight() {
		return isRight;
	}
	public void setRight(boolean isRight) {
		this.isRight = isRight;
	}
	public void setLeft(boolean isLeft) {
		this.isLeft = isLeft;
	}
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
}

