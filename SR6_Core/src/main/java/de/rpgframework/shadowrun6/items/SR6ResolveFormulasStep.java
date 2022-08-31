package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.List;

import org.prelle.simplepersist.StringValueConverter;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.IItemAttribute;
import de.rpgframework.genericrpg.items.ItemAttributeDefinition;
import de.rpgframework.genericrpg.items.ItemAttributeFloatValue;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.genericrpg.items.Usage;
import de.rpgframework.genericrpg.items.formula.FormulaImpl;
import de.rpgframework.genericrpg.items.formula.FormulaTool;
import de.rpgframework.genericrpg.items.formula.VariableResolver;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.genericrpg.persist.IntegerConverter;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * Do special handling of attributes not present in Generic RPGFramework
 * @author prelle
 *
 */
public class SR6ResolveFormulasStep implements CarriedItemProcessor {
	
	final static Logger logger = SR6GearTool.logger;

	//-------------------------------------------------------------------
	/**
	 */
	public SR6ResolveFormulasStep() {
		// TODO Auto-generated constructor stub
	}
	
	private void applyResolved(CarriedItem<?> model, String resolvedString, FormulaImpl formula, ItemAttributeDefinition val) {
		IItemAttribute attrib = val.getModifyable();
		if (val.getLookupTable()!=null) {
			logger.log(Level.DEBUG, "Lookup ''{0}'' in table {1}", resolvedString, Arrays.toString(val.getLookupTable()));
			String tmp = val.getLookupTable()[Integer.parseInt(resolvedString)-1];
			logger.log(Level.DEBUG, "Resolved {0} maps to {1}", resolvedString,tmp);
			resolvedString = tmp;
		}
		// Convert to expected value
		logger.log(Level.DEBUG, "{0}: Handing ''{1}'' to converter {2}", attrib.name(), resolvedString, attrib.getConverter());
		try {
			StringValueConverter<?> conv = attrib.getConverter();
			if (conv==null)
				conv = new IntegerConverter();
			Object resolved = conv.read(resolvedString);

			logger.log(Level.INFO, val.getModifyable() + ": RAW " + val.getRawValue() + " ==> " + formula + " ==> "
					+ resolved);
			if (resolved instanceof Integer) {
				ItemAttributeNumericalValue<?> aVal = new ItemAttributeNumericalValue<>(
						val.getModifyable());
				aVal.setDistributed((int) resolved);
				model.setAttribute(aVal.getModifyable(), aVal);
				logger.log(Level.DEBUG, "Set attribute "+val+" to numerical "+aVal+" because type is "+resolved.getClass());
			} else if (resolved instanceof Float) {
				ItemAttributeFloatValue<?> aVal = new ItemAttributeFloatValue<>(
						val.getModifyable());
				aVal.setDistributed((float) resolved);
				model.setAttribute(aVal.getModifyable(), aVal);
				logger.log(Level.DEBUG, "Set attribute "+val+" to float "+aVal+" because type is "+resolved.getClass());
			} else {
				ItemAttributeObjectValue<?> aVal = new ItemAttributeObjectValue<>(val);
				aVal.setValue(resolved);
				model.setAttribute(aVal.getModifyable(), aVal);
				logger.log(Level.DEBUG, "Set attribute "+val+" to object "+aVal+" because type is "+resolved.getClass());
			}
		} catch (Exception e) {
			logger.log(Level.ERROR, "Failed converting '" + resolvedString + "' using " + attrib.getConverter()
					+ " of attribute " + attrib, e);
//			ret.addMessage(new ToDoElement(Severity.STOPPER, "Failed converting '" + resolvedString
//					+ "' using " + attrib.getConverter() + " of attribute " + attrib));
		}
	}
	
	//-------------------------------------------------------------------
	@SuppressWarnings({ "rawtypes" })
	private void doMagic(Lifeform user, CarriedItem<?> model, FormulaImpl formula, ItemAttributeDefinition val) {
		IItemAttribute attrib = val.getModifyable();
		if (!formula.isResolved()) {
			String resolvedString = FormulaTool.resolve(ShadowrunReference.ITEM_ATTRIBUTE, formula, new VariableResolver(model, user));
			if (resolvedString!=null) {
				applyResolved(model, resolvedString, formula, val);
//			} else {
//				logger.log(Level.DEBUG, "Not resolved yet: "+formula);
			}
		} else {
//			logger.log(Level.WARNING, "{0}:{1} is already resolved", val.getModifyable(), val.getFormula());
			if (formula.isFloat())
				applyResolved(model, String.valueOf(formula.getAsFloat()), formula, val);
			else if (formula.isInteger())
				applyResolved(model, String.valueOf(formula.getAsInteger()), formula, val);
			else
				applyResolved(model, formula.getAsString(), formula, val);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(java.lang.String, de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public OperationResult<List<Modification>> process(String indent, ModifiedObjectType ref, Lifeform user, CarriedItem<?> model, List<Modification> unprocessed) {
		OperationResult<List<Modification>> ret = new OperationResult<>(unprocessed);
		
		ItemTemplate template = (ItemTemplate) model.getResolved();
		if (template==null) {
			logger.log(Level.ERROR, "No resolved template for ''{0}'' in item {1}", model.getKey(), model.getUuid());
			System.exit(1);
		}
		
		// Copy attributes from <usage>
		Usage usage = template.getUsage(model.getCarryMode());
//		logger.log(Level.ERROR, "Main usage for mode "+model.getCarryMode()+" is "+usage);
//		logger.log(Level.ERROR, "variant is "+model.getVariant());
		if (model.getVariant()!=null && model.getVariant().getUsage(model.getCarryMode())!=null)
			usage = model.getVariant().getUsage(model.getCarryMode());
		if (usage!=null && usage.getFormula()!=null) {
			ItemAttributeDefinition val = new ItemAttributeDefinition(SR6ItemAttribute.ESSENCECOST, usage.getFormula());
			doMagic(user, model, (FormulaImpl) usage.getFormula(), val);
		}
		
		return ret;
	}

}
