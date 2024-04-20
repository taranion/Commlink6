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
	private <E extends DataItem> List<E> loadList(DataSetEntry<E> file, DataSet set, CustomDataSetHandle handle) {
		CustomDataSetManager manager = CustomDataSetManagerLoader.getInstance();
		InputStream data = manager.getDataFile(handle, file.key());
		Class<E> clsItem = file.clazz();
		Class<? extends List<E>> clsList = file.listClazz();
		try {
			List<E> dataList = GenericCore.loadDataItems(
					clsList, 
					clsItem, set, data);
			logger.log(Level.INFO, "  Custom Data: Loaded {0} elements of {1}", dataList.size(), file.clazz());
			return dataList;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			logger.log(Level.INFO, "Custom Data: Load {0}", set.getLocales());
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
