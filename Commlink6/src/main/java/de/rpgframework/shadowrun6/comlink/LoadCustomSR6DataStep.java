package de.rpgframework.shadowrun6.comlink;

import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.core.StartupStep;
import de.rpgframework.genericrpg.data.CustomDataSetHandle;
import de.rpgframework.genericrpg.data.CustomDataSetHandle.DataSetEntry;
import de.rpgframework.genericrpg.data.CustomDataSetManager;
import de.rpgframework.genericrpg.data.CustomDataSetManagerLoader;
import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.DataSet;
import de.rpgframework.genericrpg.data.GenericCore;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemTemplateList;

/**
 * @author prelle
 *
 */
public class LoadCustomSR6DataStep implements StartupStep {

	private static Logger logger = System.getLogger("de.rpgframework.shadowrun6.data");

	//-------------------------------------------------------------------
	public LoadCustomSR6DataStep() {
	}
	
	//-------------------------------------------------------------------
	private static Class<? extends DataItem> guessItemType(String name) {
		if (name.equals("hello") || name.startsWith("gear"))
			return ItemTemplate.class;
		
		return null;
	}
	
	//-------------------------------------------------------------------
	private static Class<? extends List> guessListType(String name) {
		if (name.equals("hello") || name.startsWith("gear"))
			return ItemTemplateList.class;
		
		return null;
	}
	
	//-------------------------------------------------------------------
	private <E extends DataItem> List<E> loadList(DataSetEntry<E> file, DataSet set, CustomDataSetHandle handle) {
		CustomDataSetManager manager = CustomDataSetManagerLoader.getInstance();
		InputStream data = manager.getDataFile(handle, file.key());
		if (data==null) {
			logger.log(Level.ERROR, "Did not get inputstream for custom data {0}", file.key());
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 1, "Error accessing custom data for "+file.key());
			return null;
		}
		Class<E> clsItem = file.clazz();
		Class<? extends List<E>> clsList = file.listClazz();
		if (clsItem==null) clsItem = (Class<E>) guessItemType(file.key());
		if (clsList==null) clsList = (Class<? extends List<E>>) guessListType(file.key());
		if (clsItem==null) {
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 1, "Missing class type for custom data "+file.key());
			return null;
		}
		if (clsList==null) {
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 1, "Missing list class type for custom data "+file.key());
			return null;
		}
		try {
			List<E> dataList = GenericCore.loadDataItems(
					clsList, 
					clsItem, set, data);
			logger.log(Level.INFO, "  Custom Data: Loaded {0} elements of {1}", dataList.size(), file.clazz());
			return dataList;
		} catch (IOException e) {
			logger.log(Level.ERROR, "Error loading "+file.key(),e);
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Error loading custom data "+file.key()+" as "+clsList.getSimpleName()+"\n"+e.getMessage(),e);
		}	
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		CustomDataSetManager manager = CustomDataSetManagerLoader.getInstance();
		if (manager==null) {
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "No custom data handler found");
			return;
		}
		List<CustomDataSetHandle> list = manager.getCustomDataProducts();
		for (CustomDataSetHandle handle : list) {
			DataSet set = handle.getName();
			logger.log(Level.INFO, "Custom Data: Load {0}", set.getID(),set.getLocales());
			for (DataSetEntry<?> file : handle.getOrderedFileKeys()) {			
				loadList(file, set, handle);				
			}
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.StartupStep#canRun()
	 */
	@Override
	public boolean canRun() {
		return true;
	}

}
