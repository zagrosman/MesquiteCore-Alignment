/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.molec.TopBlastMatches; import java.awt.*;import java.net.*;import java.io.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.table.*;import mesquite.categ.lib.*;import mesquite.molec.lib.*;/* ======================================================================== */public class TopBlastMatches extends CategDataSearcher { 	MesquiteTable table;	CharacterData data;	StringBuffer results;	StringArray accessionNumbers;	boolean importTopMatches = false;	boolean saveResultsToFile = true;	int maxHits = 1;	double  minimumBitScore = 0.0;	boolean preferencesSet = false;	boolean fetchTaxonomy = false;	int maxTime = 300;	static int upperMaxHits = 30;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName){		loadPreferences();		results = new StringBuffer();		results.append("Sequence\tTop Match\tAccession\tScore\tE Value");		accessionNumbers = new StringArray(20);		return true;	}	/*.................................................................................................................*/	public void processSingleXMLPreference (String tag, String content) {		if ("fetchTaxonomy".equalsIgnoreCase(tag))			fetchTaxonomy = MesquiteBoolean.fromTrueFalseString(content);		else if ("saveResultsToFile".equalsIgnoreCase(tag))			saveResultsToFile = MesquiteBoolean.fromTrueFalseString(content);		else if ("importTopMatches".equalsIgnoreCase(tag))			importTopMatches = MesquiteBoolean.fromTrueFalseString(content);		else if ("maxTime".equalsIgnoreCase(tag))			maxTime = MesquiteInteger.fromString(content);		else if ("maxHits".equalsIgnoreCase(tag))			maxHits = MesquiteInteger.fromString(content);				preferencesSet = true;	}	/*.................................................................................................................*/	public String preparePreferencesForXML () {		StringBuffer buffer = new StringBuffer(60);			StringUtil.appendXMLTag(buffer, 2, "fetchTaxonomy", fetchTaxonomy);  		StringUtil.appendXMLTag(buffer, 2, "maxTime", maxTime);  		StringUtil.appendXMLTag(buffer, 2, "importTopMatches", importTopMatches);  		StringUtil.appendXMLTag(buffer, 2, "maxHits", maxHits);  		StringUtil.appendXMLTag(buffer, 2, "saveResultsToFile", saveResultsToFile);  		preferencesSet = true;		return buffer.toString();	}	/*.................................................................................................................*/	public boolean queryOptions() {		MesquiteInteger buttonPressed = new MesquiteInteger(1);		ExtensibleDialog dialog = new ExtensibleDialog(MesquiteTrunk.mesquiteTrunk.containerOfModule(), "Top Blast Matches",buttonPressed);  //MesquiteTrunk.mesquiteTrunk.containerOfModule()		dialog.addLabel("Options for Top Blast Matches");		IntegerField maxHitsField = dialog.addIntegerField("Maximum number of hits:",  maxHits,5,1,upperMaxHits);		Checkbox saveFileCheckBox = dialog.addCheckBox("save results to file",saveResultsToFile);		Checkbox fetchTaxonomyCheckBox = dialog.addCheckBox("fetch taxonomic lineage",fetchTaxonomy);		Checkbox importCheckBox = dialog.addCheckBox("import top matches into matrix",importTopMatches);		IntegerField maxTimeField = dialog.addIntegerField("Maximum time for BLAST response (seconds):",  maxTime,5);		dialog.completeAndShowDialog(true);		if (buttonPressed.getValue()==0)  {			maxHits = maxHitsField.getValue();			saveResultsToFile = saveFileCheckBox.getState();			fetchTaxonomy = fetchTaxonomyCheckBox.getState();			importTopMatches = importCheckBox.getState();			maxTime=maxTimeField.getValue();			storePreferences();		}		dialog.dispose();		return (buttonPressed.getValue()==0);	}	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */	public boolean requestPrimaryChoice(){		return true;  	}	/*.................................................................................................................*/	/** returns the version number at which this module was first released.  If 0, then no version number is claimed.  If a POSITIVE integer	 * then the number refers to the Mesquite version.  This should be used only by modules part of the core release of Mesquite.	 * If a NEGATIVE integer, then the number refers to the local version of the package, e.g. a third party package*/	public int getVersionOfFirstRelease(){		return 110;  	}	/*.................................................................................................................*/	public boolean isPrerelease(){		return false;	}	/*.................................................................................................................*/	/** Called to search on the data in selected cells.  Returns true if data searched*/	public boolean searchData(CharacterData data, MesquiteTable table, CommandRecord commandRec){		this.data = data;		if (!(data instanceof DNAData || data instanceof ProteinData)){			discreetAlert(commandRec, "Only DNA or protein data can be searched using this module.");			return false;		} 		else {			if (!queryOptions())				return false;			boolean searchOK = searchSelectedTaxa(data,table, commandRec);			logln("\nSearch results: \n"+ results.toString());			if (importTopMatches) {				logln("About to import top matches.", commandRec, true);				StringBuffer report = new StringBuffer();				NCBIUtil.fetchGenBankSequencesFromAccessions(accessionNumbers.getFilledStrings(),data, commandRec,  this, true, report);					logln(report.toString());			}			if (saveResultsToFile)				saveResults(commandRec, results);			return searchOK;		}	}	/*.................................................................................................................*/	public boolean isNucleotides(CharacterData data){		return data instanceof DNAData;	}	/*.................................................................................................................*/	public boolean acceptableHit(int hitCount, double bitScore, double eValue) {		return hitCount<=maxHits;	}	/*.................................................................................................................*/	public void recoverResults(String response, CommandRecord commandRec){		Parser parser = new Parser();		parser.setString(response);		if (!parser.isXMLDocument(false))   // check if XML			return;		MesquiteString nextTag = new MesquiteString();		String tagContent;		String accession="";		double bitScore = 0.0;		double eValue = 0.0;		if (parser.resetToXMLTagContents("BLASTOUTPUT"))			if (parser.resetToXMLTagContents("BLASTOUTPUT_iterations")) 				if (parser.resetToXMLTagContents("Iteration") && parser.resetToXMLTagContents("Iteration_hits")){					tagContent = parser.getNextXMLTaggedContent(nextTag);					int hitCount = 0;					while (!StringUtil.blank(nextTag.getValue()) && hitCount<maxHits) {						if ("Hit".equalsIgnoreCase(nextTag.getValue())) {   // here is a hit							String tax=null;							StringBuffer tempBuffer = new StringBuffer();							hitCount++;							Parser hitParser = new Parser(tagContent);							tagContent = hitParser.getNextXMLTaggedContent(nextTag);							while (!StringUtil.blank(nextTag.getValue())) {								if ("Hit_def".equalsIgnoreCase(nextTag.getValue())) {  									tempBuffer.append(StringUtil.stripTrailingWhitespace(tagContent)+"\t");								}								else if ("Hit_accession".equalsIgnoreCase(nextTag.getValue())) {  									tempBuffer.append(StringUtil.stripTrailingWhitespace(tagContent)+"\t");									accession=tagContent;									if (fetchTaxonomy) {										tax = NCBIUtil.fetchTaxonomyList(accession, data, commandRec, this, true, null);									}								}								else if ("Hit_hsps".equalsIgnoreCase(nextTag.getValue())) {  									Parser subParser = new Parser(tagContent);									tagContent = subParser.getNextXMLTaggedContent(nextTag);									while (!StringUtil.blank(nextTag.getValue())) {										if ("Hsp".equalsIgnoreCase(nextTag.getValue())) {											subParser.setString(tagContent);											tagContent = subParser.getNextXMLTaggedContent(nextTag);											while (!StringUtil.blank(nextTag.getValue())) {												if ("Hsp_bit-score".equalsIgnoreCase(nextTag.getValue())) {													tempBuffer.append(StringUtil.stripTrailingWhitespace(tagContent)+"\t");													bitScore = MesquiteDouble.fromString(tagContent);												}												else if ("Hsp_evalue".equalsIgnoreCase(nextTag.getValue())) {													tempBuffer.append(StringUtil.stripTrailingWhitespace(tagContent)+"\t");													eValue = MesquiteDouble.fromString(tagContent);												}												tagContent = subParser.getNextXMLTaggedContent(nextTag); 											}										}										tagContent = subParser.getNextXMLTaggedContent(nextTag); 									}								} 								tagContent = hitParser.getNextXMLTaggedContent(nextTag);							}							if (acceptableHit(hitCount, bitScore, eValue)) {								if (!StringUtil.blank(tax))									tempBuffer.append(tax+"\t");								results.append(tempBuffer.toString());								if (hitCount<maxHits || (maxHits>1 && hitCount==maxHits)) results.append("\n\t");								if (accessionNumbers.indexOfIgnoreCase(accession)<0)  // then this is a new one, add it to list									accessionNumbers.addAndFillNextUnassigned(accession);							}						}						tagContent = parser.getNextXMLTaggedContent(nextTag);					}				}	}	/*.................................................................................................................*/	protected String getRID(String response, MesquiteInteger responseTime) {		String  s = StringUtil.getAllAfterSubString(response, "!--QBlastInfoBegin");		responseTime.setValue(0);		if (!StringUtil.blank(s)) {			int valuesAcquired = 0;			String rid = null;			Parser parser = new Parser(s);			parser.setPunctuationString("=");			String token = parser.getNextToken();			while (!StringUtil.blank(token)) {				if ("RID".equalsIgnoreCase(token)) {					token = parser.getNextToken(); // = sign					rid = parser.getNextToken(); // RID					valuesAcquired++;				}				else if ("RTOE".equalsIgnoreCase(token)) {					token = parser.getNextToken(); // = sign					token = parser.getNextToken(); //response time					responseTime.setValue(token);					valuesAcquired++;				}				if (valuesAcquired>=2)					return rid;				token = parser.getNextToken();			}		}		return null;	}	/*.................................................................................................................*/	protected URL getESearchAddress(String s)	throws MalformedURLException {		return new URL(s);	}	/*.................................................................................................................*/	public String getGetQueryURL(String rid, int numDesc, CommandRecord commandRec){		String url = "http://www.ncbi.nlm.nih.gov/blast/Blast.cgi?"+NCBIUtil.getMesquiteGenBankURLMarker();		url += "&CMD=Get&RID="+rid+"&FORMAT_TYPE=XML";		url += "&DESCRIPTIONS="+numDesc + "&ALIGNMENTS="+ maxHits;		return url;	}	/*.................................................................................................................*/	public void saveResults(CommandRecord commandRec, StringBuffer results) {		String path;		path = MesquiteFile.saveFileAsDialog("Save Top Matches report", new StringBuffer("blastMatches.txt"));		if (!StringUtil.blank(path)) {			MesquiteFile.putFileContents(path, results.toString(), false);		}	}	/*.................................................................................................................*/	public void searchOneTaxon(CharacterData data, int it, int icStart, int icEnd, CommandRecord commandRec){		StringBuffer report = new StringBuffer();		String searchString = NCBIUtil.getPutQueryURL(data,it,icStart,icEnd,maxHits,commandRec, report);		logln(report.toString());		if (!StringUtil.blank(searchString)) {			try {				String taxonName = "";				Taxa taxa = data.getTaxa();				Taxon t = taxa.getTaxon(it);				if (t!=null)					taxonName = t.getName();				logln("\nBLASTing for "+taxonName, commandRec, true);				URL queryURL = new URL(searchString);				URLConnection connection = queryURL.openConnection();				InputStream in = connection.getInputStream();				logln("Processing initial response", commandRec, true);				StringBuffer sb = new StringBuffer();				int c;				while ((c = in.read()) != -1) {					sb.append((char) c);				}				in.close();				MesquiteInteger responseTime = new MesquiteInteger();				String rid = getRID(sb.toString(), responseTime);				logln("Request ID of BLAST query acquired", commandRec, true);				logln("     = " + rid);				logln("   Expected time of completion of BLAST is " + responseTime.toString()+ " seconds.", commandRec, true);				int checkInterval = responseTime.getValue();				if (checkInterval<10) checkInterval = 10;				//pauseForSeconds(responseTime.getValue()-checkInterval);				int count=0;				String response="";				String recoverURLString = getGetQueryURL(rid,1,commandRec);				MesquiteTimer timer = new MesquiteTimer();				timer.start(); 				int totalTime = 0;				if (!StringUtil.blank(recoverURLString)) {					while (!NCBIUtil.responseSaysBLASTIsReady(response) && totalTime<maxTime) {						int seconds = (int)(timer.timeSinceVeryStart()/1000);						if (count>0) logln(" Not done.  (" + seconds + " seconds.)");						int waitSeconds;						if (count==0) waitSeconds = checkInterval+2;						else if (count==1) waitSeconds = 3;						else waitSeconds = checkInterval;						for (int i = 0; i<waitSeconds*10; i++) {							Thread.sleep(100);							if (i%10==0) {								int sec = seconds+ i/10;								commandRec.tick("Waiting for BLAST. ("+sec + ")");							}						}						log("   Querying to see if BLAST has completed.  ", commandRec, true);						queryURL = new URL(recoverURLString);						connection = queryURL.openConnection();						in = connection.getInputStream();						StringBuffer responseBuffer = new StringBuffer();						while ((c = in.read()) != -1) {							responseBuffer.append((char) c);						}						in.close();						response = responseBuffer.toString();						count++;						totalTime +=checkInterval;					}					commandRec.tick("");					if (NCBIUtil.responseSaysBLASTIsReady(response)) logln(" Done."); else logln(" Not completed in time.");					results.append("\n" + taxonName + "\t");					recoverResults(response, commandRec);				}				timer.end();			}			catch (Exception e) {				logln("Connection error");			}		}	}	public CompatibilityTest getCompatibilityTest(){		return new RequiresAnyMolecularData();	}	/*.................................................................................................................*/	public String getNameForMenuItem() {		return "Top BLAST Matches...";	}	/*.................................................................................................................*/	public String getName() {		return "Top BLAST Matches";	}	/*.................................................................................................................*/	public boolean showCitation() {		return false;	}	/*.................................................................................................................*/	public String getExplanation() {		return "Does a blast search on selected data and returns the top blast matches for each sequence selected.";	}}/*Search string into Genbank Entrez:http://www.ncbi.nlm.nih.gov:80/entrez/query.fcgi?cmd=Search&db=nucleotide&dopt=GenBank&term=Bembidionhttp://www.ncbi.nlm.nih.gov/blast/Blast.cgi?DATABASE=nr&FORMAT_TYPE=HTML&PROGRAM=blastn&CLIENT=web&SERVICE=plain&PAGE=Nucleotides&CMD=Put&QUERY= http://www.ncbi.nlm.nih.gov/blast/Blast.cgi?DATABASE=nr&HITLIST_SIZE=10&FILTER=L&EXPECT=10&FORMAT_TYPE=HTML&PROGRAM=blastn&CLIENT=web&SERVICE=plain&NCBI_GI=on&PAGE=Nucleotides&CMD=Put&QUERY= http://www.ncbi.nlm.nih.gov/blast/Blast.cgi?CMD=put&PAGE=Nucleotides&program=blast&QUERY_FILE=fasta&query="CCAAGTCCTTCTTGAAGGGGGCCATTTACCCATAGAGGGTGCCAGGCCCGTAGTGACCATTTATATATTTGGGTGAGTTTCTCCTTAGAGTCGGGTTGCTTGAGAGTGCAGCTCTAAGTGGGTGGTAAACTCCATCTAAGGCTAAATATGACTGCGAAACCGATAGCGAACAAGTACCGTGAGGGAAAGTTGAAAAGAACTTTGAAGAGAGAGTTCAAGAGTACGTGAAACTGTTCAGGGGTAAACCTGTGGTGCCCGAAAGTTCGAAGGGGGAGATTC" */