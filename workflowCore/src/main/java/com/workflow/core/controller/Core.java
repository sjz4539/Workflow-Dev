package com.workflow.core.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.workflow.core.controller.library.Library;
import com.workflow.core.controller.library.RemovableLibrary;
import com.workflow.core.model.account.Account;
import com.workflow.core.model.resource.ResourceTaskService;
import com.workflow.core.model.resource.cache.ResourceCache;
import com.workflow.core.view.IWorkflowGUI;
import com.workflow.core.view.IWorkflowGUIFactory;

/**
 * Core logic class. Handles initialization and
 * ties the whole program together. Also provides
 * core module classes with a central access point to
 * platform-specific things (ex: GUI)
 * 
 * @author Steven Zuchowski
 */
public class Core{
	
	public static final String CONFIG_FILE_NAME = "config.properties";
	public static final String LIBRARY_FILE_NAME = "config.libraries";
	
	//config properties
	public static final String PROP_CACHE_ROOT = "cache_root";
	public static final String DEF_CACHE_ROOT = "";
	//public static final String PROP_MAX_SIMULTANEOUS_DOWNLOADS = "max_simul_downloads";
	//public static final String DEF_MAX_SIMULTANEOUS_DOWNLOADS = "10";
	//public static final String PROP_MAX_SIMULTANEOUS_UPLOADS = "max_simul_uploads";
	//public static final String DEF_MAX_SIMULTANEOUS_UPLOADS = "10";
	public static final String PROP_MAX_SIMULTANEOUS_TRANSFERS = "max_simul_transfers";
	public static final String DEF_MAX_SIMULTANEOUS_TRANSFERS = "10";
	
	//singleton core instance
	private static Core core = null;
	
	//member variables
	//declared static to shorten internal references (no more Core.thing inside core)
	private static IPlatformHelper platHelper = null;
	private static IWorkflowGUI gui = null;
	private static IWorkflowGUIFactory guiFactory = null;
	private static IProcessMonitor procMon = null;
	private static ResourceCache resCache = null;
	private static Properties properties;
	private static Library curLibrary;
	private static ArrayList<Library> libraries;
	private static ArrayList<Account> accounts;
	
//============================================================================
	
	//--Library Functions--//
	
	/**
	 * Initializes Core, reading/creating settings and setting up instance variables. Must be called for Core to be reliable.
	 * @param wg The platform-specific implementation of IWorkflowGUI, main graphical interface.
	 * @param ph The platform-specific implementation of IPlatformHelper, provides access to other platform-specific class implementations.
	 * @return True if initialization succeeds, false otherwise.
	 */
	public static boolean init(IWorkflowGUI wg, IPlatformHelper ph){
		getCore();
		gui = wg;
		platHelper = ph;
		return core.instanceInit();
	}

	/**
	 * Singleton accessor method. Init() must be called first for the result to be reliable.
	 * @return The singleton instance of Core.
	 */
	public static Core getCore(){
		if(core == null){
			core = new Core();
		}
		return core;
	}
	
	/**
	 * @return The IWorkflowGUI instance provided when init() was called.
	 */
	public static IWorkflowGUI getGui(){
		return gui;
	}

	/**
	 * @return A singleton implementation of IWorkflowGUIFactory, used by core module classes to interact with platform-specific GUI classes.
	 */
	public static IWorkflowGUIFactory getGuiFactory(){
		if(guiFactory == null){ 
			guiFactory = platHelper.getGuiFactory();
		}
		return guiFactory;
	}
	
	/**
	 * @return A singleton implementation of IProcessMonitor, used by core module classes to create/start new processes.
	 */
	public static IProcessMonitor getProcessMonitor(){
		if(procMon == null){
			procMon = platHelper.getProcessMonitor();
		}
		return procMon;
	}

	/**
	 * @return The IPlatformHelper instance provided when init() was called. 
	 * Used to get platform-specific class implementations or perform platform-specific logic tasks.
	 */
	public static IPlatformHelper getPlatformHelper(){
		return platHelper;
	}

	/**
	 * @return The singleton ResourceCache instance.
	 */
	public static ResourceCache getResourceCache(){
		if(resCache == null){
			if(core.getProperty(PROP_CACHE_ROOT) == null || core.getProperty(PROP_CACHE_ROOT).isEmpty()){
				core.setProperty(PROP_CACHE_ROOT, ResourceCache.genCacheFolder());
				core.saveSettings();
				return getResourceCache();
			}else{
				resCache = new ResourceCache(core.getProperty(PROP_CACHE_ROOT));
			}
		}
		return resCache;
	}
	
//============================================================================

	//--Public Instance Functions--//
	
	/**
	 * Reads/creates program settings and data, plus sets up the local cache.
	 * @return True if these tasks succeed, false otherwise.
	 */
	public boolean instanceInit(){
		//read in settings, set up listeners/handlers/etc
		//if any errors occur, return false.
		return readSettings() && loadRemoteLibraryList() && createCache();
	}
	
	/**
	 * @param propKey A property name. See class constants.
	 * @return The value of the specified property, if it exists. Null otherwise.
	 */
	public String getProperty(String propKey){
		return properties.getProperty(propKey);
	}
	
	/**
	 * Assigns a property value to a property name. Property names should be constants.
	 * @param propKey The String name of the property to be set.
	 * @param propVal The String value of the property to be set.
	 */
	public void setProperty(String propKey, String propVal){
		properties.setProperty(propKey, propVal);
	}
	
	/**
	 * Adds an Account to the account list.
	 * @param a An Account.
	 * (rocket science)
	 */
	public void addAccount(Account a){
		if(!accounts.contains(a)){
			accounts.add(a);
		}
	}
	
	/**
	 * @param i The index of an account in the account list.
	 * @return The account at index i, if it exists. Null otherwise.
	 */
	public Account getAccount(int i){
		if( i >= 0 && accounts.size() > i ){
			return accounts.get(i);
		}else{
			return null;
		}
	}
	
	/**
	 * @return The list of all Accounts.
	 */
	public ArrayList<Account> getAccounts(){
		return accounts;
	}
	
	/**
	 * Removes an Account from the list of accounts by index.
	 * @param i The index of the account to be removed.
	 * @return The Account that was removed, if one was. Null otherwise.
	 */
	public Account removeAccount(int i){
		if(i >= 0 && accounts.size() > i){
			return accounts.remove(i);
		}else{
			return null;
		}
	}
	
	/**
	 * Removes an Account from the list of accounts.
	 * @param a The Account to remove.
	 */
	public void removeAccount(Account a){
		accounts.remove(a);
	}
	
	/**
	 * Adds a Library to the list of libraries.
	 * @param library The Library to add to the list.
	 * @param setCurrent If true, this Library will be set as the currently active library.
	 */
	public void addLibrary(Library library, boolean setCurrent){
		libraries.add(library);
		getGui().updateLibraryList();
		if(setCurrent){
			setCurrentLibrary(library);
		}
	}
	
	/**
	 * @param i The index of a Library in the list of libraries.
	 * @return The Library at index i, if one exists. Null otherwise.
	 */
	public Library getLibrary(int i){
		return libraries.get(i);
	}
	
	/**
	 * @return The list of all Libraries.
	 */
	public List<Library> getLibraries(){
		return libraries;
	}
	
	/**
	 * Removes a Library from the list of libraries.
	 * @param l The Library to be removed.
	 */
	public void removeLibrary(Library l){
		libraries.remove(l);
		if(getCurrentLibrary().equals(l)){
			setCurrentLibrary(null);
		}
		getGui().updateLibraryList();
	}
	
	/**
	 * Sets a Library as the currently active library.
	 * @param l The Library to set as the active library.
	 */
	public void setCurrentLibrary(Library l){
		if(l != null){
			if(l.isLoaded() || l.loadTasks()){
				Library oldLib = curLibrary;
				curLibrary = l;
				curLibrary.getMenu().updateUI();
				if(oldLib != null){
					oldLib.getMenu().updateUI();
				}
				getGui().setLibraryView(curLibrary.getView());
			}else if(l.getType() == Library.Type.REMOTE){
				//attempt to load the library first, then set it as active after.
				final Library lib = l;
				ResourceTaskService.loadResource(
					l.getResource(), 
					new SimpleHandler(){
						public void handle() {
							setCurrentLibrary(lib);
						}
					},
					null
				);
			}
		}else{
			getGui().setLibraryView(null);
		}
	}
	
	/**
	 * @return The currently active Library.
	 */
	public Library getCurrentLibrary(){
		return curLibrary; 
	}
	
//============================================================================
	
	//--Instance Functions--//
	
	private Core(){
	}
	
	/**
	 * Reads in the properties for this program.
	 * If they cannot be found, they will be created using defaults.
	 * @return True if this task succeeds, false otherwise.
	 */
	public boolean readSettings(){
		File config = new File(getPlatformHelper().getStorageRoot() + File.separator + Core.CONFIG_FILE_NAME);
		
		if(properties == null){
			properties = new Properties();
		}else{
			properties.clear();
		}
		
		if(config.exists()){
			try {
				FileReader reader = new FileReader(config);
				properties.load(reader);
				reader.close();
				return true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}else{
			return Core.getCore().generateConfigFile();
		}
	}
	
	/**
	 * Writes this program's properties to a file.
	 * @return True if this task succeeds, false otherwise.
	 */
	public boolean saveSettings(){
		File config = new File(getPlatformHelper().getStorageRoot() + File.separator + Core.CONFIG_FILE_NAME);
		
		try {
			FileWriter writer = new FileWriter(config);
			properties.store(writer, "");
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Sets properties to defaults. If no Properties
	 * object exists, one is created.
	 */
	private void setDefaultSettings(){
		if(properties == null){
			properties = new Properties();
		}
		
		//set defaults, define default values alongside key strings
		//properties.setProperty(PROP_MAX_SIMULTANEOUS_DOWNLOADS, DEF_MAX_SIMULTANEOUS_DOWNLOADS);
		//properties.setProperty(PROP_MAX_SIMULTANEOUS_UPLOADS, DEF_MAX_SIMULTANEOUS_UPLOADS);
		properties.setProperty(PROP_MAX_SIMULTANEOUS_TRANSFERS, DEF_MAX_SIMULTANEOUS_TRANSFERS);
		
	}
	
	/**
	 * Creates a properties object with default values,
	 * then writes it to disk.
	 * @return True if the operation succeeds, false otherwise.
	 */
	public boolean generateConfigFile(){
		setDefaultSettings();
		return saveSettings();
	}
	
	/**
	 * Loads the list of remote libraries from local storage if one exists.
	 * @return True if the operation succeeds, false otherwise.
	 */
	private boolean loadRemoteLibraryList(){
		if(libraries == null){
			libraries = new ArrayList<Library>();
		}
		
		File libraryFile = new File(LIBRARY_FILE_NAME + Library.LIBRARY_FILE_EXTENSION);
		
		if(libraryFile.exists()){
			try {
				FileInputStream fileIn = new FileInputStream(libraryFile);
				ObjectInputStream objIn = new ObjectInputStream(fileIn);
				
				AbstractMap.SimpleEntry<ArrayList<Library>, ArrayList<Account>> data = 
						(AbstractMap.SimpleEntry<ArrayList<Library>, ArrayList<Account>>)objIn.readObject();
				
				libraries = data.getKey();
				accounts = data.getValue();
				
				objIn.close();
				fileIn.close();
				
				return true;
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
		}else{
			libraries = new ArrayList<Library>();
			accounts = new ArrayList<Account>();
			return true;
		}
	}
	
	/**
	 * Loads a library from removable storage.
	 * @param path The absolute path of the library data file to load.
	 * @return True if the operation succeeds, false otherwise.
	 */
	public RemovableLibrary loadRemovableLibrary(String path){

		File libraryFile = new File(path);
		
		if(libraryFile.exists()){
			try {
				FileInputStream fileIn = new FileInputStream(libraryFile);
				ObjectInputStream objIn = new ObjectInputStream(fileIn);
				
				RemovableLibrary importedLib = (RemovableLibrary)objIn.readObject();
				
				//adjust the library's root based on the provided path
				String oldRoot = importedLib.getRoot();
				oldRoot = (oldRoot.endsWith(File.separator) ? oldRoot.substring(0, oldRoot.length() - 1) : oldRoot);
				int pos = oldRoot.lastIndexOf(File.separator);
				String rootName = oldRoot.substring(pos + 1);
				
				//find the root folder's name in the given path.
				//set the library's root folder to everything in the given path up to and including that match.
				pos = path.lastIndexOf(rootName);
				if(pos != -1){
					importedLib.setRoot(path.substring(0, pos + rootName.length()));
					objIn.close();
					fileIn.close();
					return importedLib;
				}else{
					objIn.close();
					fileIn.close();
					return null;
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}else{
			return null;
		}
	}
	
	/**
	 * Writes all libraries to storage.
	 * @return True if the operation succeeds, false otherwise.
	 */
	public boolean saveLibraries(){
		return saveRemoteLibraryList() && saveRemovableLibraries();
	}
	
	/**
	 * Writes the list of remote libraries to local storage.
	 * @return True if the operation succeeds, false otherwise.
	 */
	public boolean saveRemoteLibraryList(){
		if(libraries == null){
			libraries = new ArrayList<Library>();
		}
		if(accounts == null){
			accounts = new ArrayList<Account>();
		}
		
		File libraryFile = new File(LIBRARY_FILE_NAME + Library.LIBRARY_FILE_EXTENSION);
		
		//generate a list of remote libraries
		ArrayList<Library> remotes = new ArrayList<Library>();
		for(Library l : libraries){
			if(!l.isRemovable()){
				remotes.add(l);
			}
		}
		
		try {
			FileOutputStream fileOut = new FileOutputStream(libraryFile);
			ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
			
			objOut.writeObject(new AbstractMap.SimpleEntry<ArrayList<Library>, ArrayList<Account>>(remotes, accounts));
			
			objOut.close();
			fileOut.close();
			
			return true;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	/**
	 * Writes all removable libraries to their storage media.
	 * @return True if the operation succeeds, false otherwise.
	 */
	private boolean saveRemovableLibraries(){
		boolean ret = true;
		for(Library l : libraries){
			if(l.isRemovable() && !saveRemovableLibraryData((RemovableLibrary)l)){
				ret = false;
			}
		}
		return ret;
	}
	
	/**
	 * Writes a removable library's data to its storage media.
	 * @param rl The RemovableLibrary to be stored.
	 * @return True if the operation succeeds, false otherwise.
	 */
	private boolean saveRemovableLibraryData(RemovableLibrary rl){
		
		try{
			
			File removableFile = new File(rl.getRoot() + File.separator + rl.getName() + Library.REMOVABLE_LIBRARY_FILE_EXTENSION);
			FileOutputStream remFileOut = new FileOutputStream(removableFile);
			ObjectOutputStream remObjOut = new ObjectOutputStream(remFileOut);
			
			remObjOut.writeObject(rl);
			
			remObjOut.close();
			remFileOut.close();
			
			return true;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			//failure, update the library status
			rl.updateStatus("Invalid save path.");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			//failure, update the library status
			rl.updateStatus("I/O error when writing this library to disk.");
			return false;
		}
	}
	
	/**
	 * Sets up a new resource cache.
	 * @return True if the operation succeeds, false otherwise.
	 */
	private boolean createCache(){
		return getResourceCache().init();
	}
	
}
