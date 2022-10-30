package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.GenericRPGTools;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.GearTool;
import de.rpgframework.genericrpg.items.PieceOfGearVariant;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.CreatePoints;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.CommonEquipmentController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6VariantMode;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class CommonEquipmentGenerator extends CommonEquipmentController  {

	private int conversionRate = 2000;
	
	//-------------------------------------------------------------------
	public CommonEquipmentGenerator(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<CarriedItem<ItemTemplate>> select(ItemTemplate value, Decision... decisions) {
		CarryMode mode = CarryMode.CARRIED;
		if (value.getUsages()!=null && value.getUsages().size()>0) {
			if (value.getUsages().size()==1)
				mode = value.getUsages().get(0).getMode();
			else if (value.getUsages().stream().map(u->u.getMode()).collect(Collectors.toList()).contains(CarryMode.IMPLANTED))
				throw new IllegalArgumentException("More than one possible CarryMode - use other select() method");
		}
		OperationResult<CarriedItem<ItemTemplate>> res = select(value, null, mode, decisions);
		parent.runProcessors();
		return res;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#select(ItemTemplate, String, Decision[])
	 */
	@Override
	public OperationResult<CarriedItem<ItemTemplate>> select(ItemTemplate value, String variantID, CarryMode mode, Decision... decisions) {
		logger.log(Level.TRACE, "ENTER select({0}, {1}", value, mode);
		try {
			Possible poss = canBeSelected(value, variantID, mode, decisions);
			if (!poss.getRequireDecisions()) {
				logger.log(Level.ERROR, "Trying to select {0} which may not be selected: {1}", value, poss.toString());
				return new OperationResult<>(poss);
			}

			PieceOfGearVariant<SR6VariantMode> variant = null;
			if (variantID!=null) {
				variant = value.getVariant(variantID);
			}

			poss =  GenericRPGTools.areAllDecisionsPresent(value, variantID, decisions);
			if (!poss.get()) {
				logger.log(Level.ERROR, "Trying to select {0} but decisions are missing: {1}", value, poss.toString());
				return new OperationResult<>(poss);
			}
			
			OperationResult<CarriedItem<ItemTemplate>> ret = SR6GearTool.buildItem(value, mode, variant, getModel(), true, decisions);
			CarriedItem<ItemTemplate> item = ret.get();
			if (value.isCountable()) item.setCount(1);
			logger.log(Level.INFO, "Add {0} to model", item.getKey());
			getModel().addCarriedItem(item);
			
			parent.runProcessors();
			return new OperationResult<CarriedItem<ItemTemplate>>(item);
		} finally {
			logger.log(Level.TRACE, "LEAVE select({0}, {1}", value, mode);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		logger.log(Level.DEBUG, "ENTER");
		try {
			Shadowrun6Character model = getModel();
			// Reset character nuyen
			model.setNuyen(0);
			conversionRate = 2000;
			todos.clear();
			
			List<Modification> unprocessed = new ArrayList<>();
			for (Modification tmp : previous) {
				if (tmp instanceof ValueModification) {
					ValueModification mod = (ValueModification)tmp;
					if (mod.getReferenceType()==ShadowrunReference.CREATION_POINTS && mod.getResolvedKey()==CreatePoints.NUYEN) {
						model.setNuyen( model.getNuyen() + mod.getValue());
						logger.log(Level.DEBUG, "consume {0}", tmp);
					} else if (mod.getReferenceType()==ShadowrunReference.GEAR) {
						ItemTemplate template = mod.getResolvedKey();
						model.setNuyen( model.getNuyen() + mod.getValue());
						logger.log(Level.DEBUG, "add {0}", template);
					} else
						unprocessed.add(tmp);
				} else if (tmp instanceof DataItemModification) {
					DataItemModification mod = (DataItemModification)tmp;
					if (mod.getReferenceType()==ShadowrunReference.GEAR) {
						ItemTemplate template = mod.getResolvedKey();
						Decision[] dec = new Decision[mod.getDecisions().size()];
						OperationResult<CarriedItem<ItemTemplate>> carry = GearTool.buildItem(template, CarryMode.EMBEDDED, getModel(), true, mod.getDecisions().toArray(dec));
						carry.get().addModification(mod);
						logger.log(Level.DEBUG, "add {0} due to {1}", template, tmp.getSource());
						model.addCarriedItem(carry.get());
					} else
						unprocessed.add(tmp);
				} else {
					// No ValueModification
					unprocessed.add(tmp);
				}
			}
			
			CommonSR6GeneratorSettings sett = getModel().getCharGenSettings(CommonSR6GeneratorSettings.class);
			if (sett.getKarmaToNuyen()>0) {
				int rate = 2000;
				int add  = sett.getKarmaToNuyen()*rate;
				logger.log(Level.INFO, "Convert {0} Karma into {1} Nuyen (Rate 1:{2})", sett.getKarmaToNuyen(), add, rate);
				model.setNuyen( model.getNuyen() + add);
				model.setKarmaFree( model.getKarmaFree() - sett.getKarmaToNuyen());
			}
			
			logger.log(Level.INFO, "{0} Nuyen available", model.getNuyen());
			
			
			
			/* Expand PACKs */
			for (CarriedItem<ItemTemplate> tmp : model.getCarriedItems()) {
				if (getItemType(tmp)==ItemType.PACK) {
					logger.log(Level.WARNING, "ToDo: handle PACK "+tmp);
				}
			}
			
			
			
			/*
			 * Walk through all items and pay for them
			 */
			int nuyen = model.getNuyen();
			for (CarriedItem<ItemTemplate> tmp : model.getCarriedItems()) {
				if (!tmp.isAutoAdded()) {
					int cost = tmp.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue();
					if (tmp.getCount()>1)
						cost *= tmp.getCount();
					//if (logger.isLoggable(Level.TRACE))
					logger.log(Level.INFO, "Pay {0} for {1}   (before {2})", cost, tmp.getKey(), nuyen);
					nuyen -= cost;
				}
				
				// If it is an weapon, check if ammunition is present, warn otherwise
				if (getItemType(tmp)==ItemType.WEAPON_FIREARMS) {
					if (Shadowrun6Tools.getAmmunitionsFor(model, tmp).isEmpty()) {
						todos.add(new ToDoElement(Severity.INFO, IRejectReasons.RES, IRejectReasons.TODO_NO_AMMUNITION, tmp.getNameWithoutRating()));
					}
				}
			}
			model.setNuyen(nuyen);
			logger.log(Level.INFO, "Nuyen remaining: {0}", model.getNuyen());
			
			return unprocessed;
		} finally {
			logger.log(Level.DEBUG, "LEAVE");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#getConvertedKarma()
	 */
	@Override
	public int getConvertedKarma() {
		return getModel().getCharGenSettings(CommonSR6GeneratorSettings.class).getKarmaToNuyen();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#getConversionRateKarma()
	 */
	@Override
	public int getConversionRateKarma() {
		return conversionRate;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#canIncreaseConversion()
	 */
	@Override
	public boolean canIncreaseConversion() {
		if (getModel().getKarmaFree()<1) return false;
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#increaseConversion()
	 */
	@Override
	public boolean increaseConversion() {
		if (!canIncreaseConversion()) {
			logger.log(Level.ERROR, "Trying to increase Karma -> Nuyen conversion although not allowed");
			return false;
		}
		
		CommonSR6GeneratorSettings sett = getModel().getCharGenSettings(CommonSR6GeneratorSettings.class);
		sett.setKarmaToNuyen(sett.getKarmaToNuyen()+1);
		logger.log(Level.INFO, "increased Karma converted to Nuyen to {0}", sett.getKarmaToNuyen());
		
		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#canDecreaseConversion()
	 */
	@Override
	public boolean canDecreaseConversion() {
		CommonSR6GeneratorSettings sett = getModel().getCharGenSettings(CommonSR6GeneratorSettings.class);
		return sett.getKarmaToNuyen()>0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#decreaseConversion()
	 */
	@Override
	public boolean decreaseConversion() {
		if (!canDecreaseConversion()) {
			logger.log(Level.ERROR, "Trying to decrease Karma -> Nuyen conversion although not allowed");
			return false;
		}
		
		CommonSR6GeneratorSettings sett = getModel().getCharGenSettings(CommonSR6GeneratorSettings.class);
		sett.setKarmaToNuyen(sett.getKarmaToNuyen()-1);
		logger.log(Level.INFO, "decreased Karma converted to Nuyen to {0}", sett.getKarmaToNuyen());
		
		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IEquipmentController#canChangeCount(de.rpgframework.genericrpg.items.CarriedItem, int)
	 */
	@Override
	public boolean canChangeCount(CarriedItem<ItemTemplate> item, int newCount) {
		// TODO Auto-generated method stub
		return false;
	}

}
