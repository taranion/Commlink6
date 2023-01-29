package de.rpgframework.shadowrun6.chargen.jfx.selector;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.ManagedDialog;
import org.prelle.javafx.NavigButtonControl;
import org.prelle.javafx.OptionalNodePane;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.CharacterController;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.ChoiceOption;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.DataItemValue;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.IReferenceResolver;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun.ASpell;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.MentorSpirit;
import de.rpgframework.shadowrun.NPCType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IFocusController;
import de.rpgframework.shadowrun.items.AugmentationQuality;
import de.rpgframework.shadowrun6.SR6NPC;
import de.rpgframework.shadowrun6.SR6RuleFlag;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.gen.CommonQualityGenerator;
import de.rpgframework.shadowrun6.chargen.jfx.pane.CarriedItemDescriptionPane;
import de.rpgframework.shadowrun6.chargen.jfx.pane.FocusValueDescriptionPane;
import de.rpgframework.shadowrun6.items.AmmunitionType;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemTypeFilter;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6ItemFlag;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class ChoiceSelectorDialog<T extends ComplexDataItem, V extends ComplexDataItemValue<T> > extends ManagedDialog implements BiFunction<T, List<Choice>, Decision[]> {

	private final static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(ChoiceSelectorDialog.class.getPackageName()+".Selectors");

	private final static Logger logger = System.getLogger(ChoiceSelectorDialog.class.getPackageName());

	private ComplexDataItemController<T,V> ctrl;
	/* Only relevant for ItemTemplates */
	private CarryMode carry;

	private OptionalNodePane optional;
	private GenericDescriptionVBox bxDesc;
	private VBox content;
	private Label lbProblem;
	private NavigButtonControl btnCtrl;

	private T item;
	private DataItemValue context;
	private SR6PieceOfGearVariant selectedVariant;
	private List<Choice> choices;
	private Map<SR6PieceOfGearVariant, List<Node>> perVariantChoices = new HashMap<>();
	private Map<Choice, Decision> decisions = new LinkedHashMap<>();
	private List<SR6ItemFlag> selectedFlasgs = new ArrayList<>();

	private List<Node> toDeleteOnMentorSpirit = new ArrayList<>();
	/** Choice to reflect the decision of the player to use the magician or adept advantages */
	private Choice magicianOrAdept = new Choice(CommonQualityGenerator.MENTOR_SPIRIT_ADVANTAGES, ShadowrunReference.MAGIC_RESO);
	private BooleanProperty chooseAdeptAdvantages = new SimpleBooleanProperty(false);
	private BooleanProperty useBothAdvantages = new SimpleBooleanProperty(false);

	//-------------------------------------------------------------------
	public ChoiceSelectorDialog(ComplexDataItemController<T,V> ctrl) {
		this(ctrl, null);
	}

	//-------------------------------------------------------------------
	public ChoiceSelectorDialog(ComplexDataItemController<T,V> ctrl, CarryMode carry) {
		this(ctrl, carry, null);
	}

	//-------------------------------------------------------------------
	public ChoiceSelectorDialog(ComplexDataItemController<T,V> ctrl, CarryMode carry, DataItemValue context) {
		super("Select",null, CloseType.CANCEL, CloseType.OK);
		this.ctrl = ctrl;
		this.carry = carry;
		this.context = context;

		content = new VBox(10);
		CharacterController<ShadowrunAttribute,Shadowrun6Character> charCtrl = ctrl.getCharacterController();
		if (ctrl instanceof ISR6EquipmentController) {
			logger.log(Level.INFO, "Use special info pane for CarriedItem");
			bxDesc = new CarriedItemDescriptionPane(r -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault()), (SR6CharacterController)charCtrl );
		} else if (ctrl instanceof IFocusController) {
			logger.log(Level.INFO, "Use special info pane for FocusValue");
			bxDesc = new FocusValueDescriptionPane(r -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault()), (SR6CharacterController)charCtrl );
		} else {
			logger.log(Level.INFO, "Use generic description pane");
			bxDesc = new GenericDescriptionVBox(
					Shadowrun6Tools.requirementResolver(Locale.getDefault()),
					Shadowrun6Tools.modificationResolver(Locale.getDefault()));
		}
		optional= new OptionalNodePane(content, bxDesc);
		lbProblem = new Label();
		lbProblem.setStyle("-fx-text-fill: -fx-accent");

		setContent(new VBox(10,optional, lbProblem));

		Shadowrun6Character model = ctrl.getModel();
		chooseAdeptAdvantages.setValue( model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesPowers());
		useBothAdvantages.set(model.hasRuleFlag(SR6RuleFlag.MENTOR_SPIRIT_BOTH_ADVANTAGES));
	}

	//-------------------------------------------------------------------
	private void showHelpFor(DataItem item) {
		System.err.println("ChoiceSelector: showHelpFor("+item.getId()+") on "+bxDesc);
		bxDesc.setData(item);
		optional.setTitle(item.getName());
	}

	//-------------------------------------------------------------------
	private void showHelpFor(DataItemValue item) {
		bxDesc.setData(item);
		optional.setTitle(item.getNameWithoutRating());
	}

	//-------------------------------------------------------------------
	private Decision[] getDecisions() {
		Decision[] ret = new Decision[decisions.size()];
		int i=0;
		for (Entry<Choice, Decision> entry : decisions.entrySet()) {
			ret[i] = entry.getValue();
			logger.log(Level.DEBUG, "Decision [{0}] = {1}", i, entry.getValue());
			i++;
		}
//		for (int i=0; i<choices.size(); i++) {
//			ret[i] = decisions.get(choices.get(i));
//		}
		return ret;
	}

	//-------------------------------------------------------------------
	public String getSelectedVariant() {
		if (selectedVariant!=null)
			return selectedVariant.getId();
		return null;
	}

	//-------------------------------------------------------------------
	private void updateButtons() {
		// Special handling for gear
		if (item instanceof ItemTemplate) {
			// Build item so far as possible
			Shadowrun6Character lifeform = ctrl.getModel();
			if (getDecisions().length>0) {
				logger.log(Level.WARNING, "Mit decision");
			}
			OperationResult<CarriedItem<ItemTemplate>> result = SR6GearTool.buildItem( (ItemTemplate)item, carry, selectedVariant, lifeform, true, (IReferenceResolver)context, getDecisions());
			logger.log(Level.WARNING, "Trying to build "+carry+" returned "+result);
			if (result.get()!=null) {
				logger.log(Level.WARNING, "with item");
				CarriedItem<ItemTemplate> carried = result.get();
				logger.log(Level.WARNING, "item has mode "+carried.getCarryMode());
				@SuppressWarnings("rawtypes")
				CharacterController c1 = ctrl.getCharacterController();
				SR6CharacterController charGen = (SR6CharacterController)c1;
//				Node info = ItemUtilJFX.getItemInfoNode(carried, charGen, true);
//				logger.log(Level.INFO, "Got info node "+info);
				logger.log(Level.WARNING, "Update description: "+bxDesc);
				bxDesc.setData(carried);
			} else
				logger.log(Level.WARNING, "Not successful");
		}

		Possible possible = null;
		if (item instanceof ItemTemplate && ctrl instanceof ISR6EquipmentController) {
			String variantID = (selectedVariant!=null)?selectedVariant.getId():null;
			possible = ((ISR6EquipmentController)ctrl).canBeSelected((ItemTemplate)item, variantID, carry, getDecisions() );
			logger.log(Level.INFO, "canBeSelected({0}) returns "+possible, carry);
		} else {
			possible = ctrl.canBeSelected(item, getDecisions() );
		}
		// Set status
		ToDoElement problem = possible.getMostSevere();
		if (problem==null) {
			lbProblem.setText(null);
		} else {
			lbProblem.setText(problem.getMessage(Locale.getDefault()));
			switch (problem.getSeverity()) {
			case STOPPER: lbProblem.setStyle("-fx-text-fill: -fx-accent"); break;
			case WARNING: lbProblem.setStyle("-fx-text-fill: primary"); break;
			case INFO   : lbProblem.setStyle("-fx-text-fill: --fx-text-base-color"); break;
			}
		}

		if (btnCtrl != null) {
			if (!possible.get() || (problem != null && problem.getSeverity() == Severity.WARNING)) {
				btnCtrl.setDisabled(CloseType.OK, true);
			} else {
				btnCtrl.setDisabled(CloseType.OK, false);
			}
		}
	}

	//-------------------------------------------------------------------
	private Label addLabel(String title) {
		Label lbName = new Label(title);
		lbName.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		content.getChildren().add(lbName);
		return lbName;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.util.function.BiFunction#apply(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Decision[] apply(T item, List<Choice> choices) {
		logger.log(Level.INFO, "ENTER apply({0}, {1})", item, choices);
		this.item = item;
		this.choices = choices;
		CloseType closed = null;
		try {
			decisions.clear();

			setTitle(ResourceI18N.format(RES, "title", item.getName()));
			bxDesc.setData(item);
			content.getChildren().clear();
			// Minimal intro text
			Label explain = new Label(ResourceI18N.get(RES, "explain"));
			explain.setWrapText(true);
			content.getChildren().add(explain);

			// Eventually prepare variants
			if ((item instanceof ItemTemplate) && !((ItemTemplate)item).getVariants().isEmpty()) {
				processVariants( (ItemTemplate)item );
			}

			for (Choice choice : choices) {
				String forceTitle = null;
				if (choice.getUUID().equals(ItemTemplate.UUID_RATING)) forceTitle=ResourceI18N.get(RES, "label.rating");
				if (choice.getUUID().equals(ItemTemplate.UUID_CHEMICAL_CHOICE)) forceTitle=ResourceI18N.get(RES, "label.chemical");
				if (choice.getI18nKey()!=null) forceTitle=null;
				processChoice(item,choice, forceTitle);
			}

			if (item instanceof ItemTemplate) {
				for (SR6ItemFlag flag : item.getUserSelectableFlags(SR6ItemFlag.class)) {
					processFlag((ItemTemplate) item, flag);
				}
			}

			btnCtrl = new NavigButtonControl();
			btnCtrl.initialize(FlexibleApplication.getInstance(), this);
			btnCtrl.setDisabled(CloseType.OK, true);
			updateButtons();
			closed = FlexibleApplication.getInstance().showAlertAndCall(this, btnCtrl);
			logger.log(Level.DEBUG, "Closed with "+closed);
			if (closed==CloseType.CANCEL)
				return null;
			return getDecisions();
		} finally {
			logger.log(Level.INFO, "LEAVE apply({0}, {1} with {2})", item, choices, closed);
		}
	}

	// -------------------------------------------------------------------
	private void processVariants(ItemTemplate template) {
		logger.log(Level.INFO, "variants detected");
		addLabel(ResourceI18N.get(RES, "label.variant"));
		ChoiceBox<SR6PieceOfGearVariant> cbVariants = new ChoiceBox<>();
		// If no item is required, add a "regular item"
		if (!template.requiresVariant()) {
			cbVariants.getItems().add(null);
		}
		cbVariants.getItems().addAll(template.getVariants());
		cbVariants.setConverter(new StringConverter<SR6PieceOfGearVariant>() {
			public SR6PieceOfGearVariant fromString(String value) { return null;}
			public String toString(SR6PieceOfGearVariant value) {
				if (value==null) return ResourceI18N.get(RES, "variant.regular");
				String name = template.getVariantName(value, Locale.getDefault());
				if (name.startsWith(template.getTypeString())) {
					ItemSubType sub = template.getAttribute(SR6ItemAttribute.ITEMSUBTYPE).getValue();
					if (sub==null) {
						name = template.getLocalizedString(Locale.getDefault(), "variant."+value.getId());
					} else {
						name = template.getLocalizedString(Locale.getDefault(), "variant."+sub.name().toLowerCase()+"."+value.getId());
					}
				}
				return name;
			}
		});
		cbVariants.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.INFO, "Chose variant {0}", n);
			selectedVariant = n;
			// Hide old variant nodes
			if (perVariantChoices.containsKey(o)) {
				logger.log(Level.DEBUG, "Hide all UI elements for old variant {0}", o);
				for (Node node : perVariantChoices.get(o)) {
					node.setVisible(false);
					node.setManaged(false);
					if (node instanceof ChoiceBox) ((ChoiceBox)node).getSelectionModel().clearSelection();
				}
			}
			// Show new variant nodes
			if (perVariantChoices.containsKey(n)) {
				logger.log(Level.DEBUG, "Show all UI elements for selected variant {0}", o);
				for (Node node : perVariantChoices.get(n)) {
					node.setVisible(true);
					node.setManaged(true);
				}
			}
			updateButtons();
		 });
		content.getChildren().add(cbVariants);

		// Make a list of choices that only exists in a variant
		for (SR6PieceOfGearVariant variant : template.getVariants()) {
			if (variant.getChoices()!=null && !variant.getChoices().isEmpty()) {
				List<Node> allVariantNodes = new ArrayList<>();
				// Prepare UI components
				for (Choice choice : variant.getChoices()) {
					String forceTitle = null;
					if (choice.getUUID().equals(ItemTemplate.UUID_RATING)) forceTitle=ResourceI18N.get(RES, "label.rating");
					if (choice.getUUID().equals(ItemTemplate.UUID_CHEMICAL_CHOICE)) forceTitle=ResourceI18N.get(RES, "label.chemical");
					List<Node> list = processChoice(item,choice, forceTitle);
					logger.log(Level.INFO, "Variant choice returned "+list);
					allVariantNodes.addAll(list);
				}
				perVariantChoices.put(variant, allVariantNodes);
				// Per default hide all variant nodes
				for (Node node : allVariantNodes) {
					node.setVisible(false);
					node.setManaged(false);
				}
			}
		}

		cbVariants.getSelectionModel().select(0);
	}

	// -------------------------------------------------------------------
	private void processFlag(ItemTemplate item, SR6ItemFlag flag) {
		logger.log(Level.DEBUG, "Flag "+flag);
		System.err.println("ChoiceSelectorDialog: Flag "+flag);
		CheckBox checkBox = new CheckBox(flag.getName());
		checkBox.selectedProperty().addListener( (ov,o,n) -> {
			if (n) {
				logger.log(Level.DEBUG, "Selected flag {0}", n);
				selectedFlasgs.add(flag);
			} else {
				logger.log(Level.DEBUG, "Deselected flag {0} again", n);
				selectedFlasgs.remove(flag);
			}
			updateButtons();
		 });
		content.getChildren().add(checkBox);
	}

	// -------------------------------------------------------------------
	private List<Node>  processChoice(ComplexDataItem item, Choice choice, String forceTitle) {
		logger.log(Level.DEBUG, "Choice " + choice);
		List<Node> ret = new ArrayList<>();

		Label label = addLabel(
				(forceTitle==null)
				?
				item.getChoiceName(choice, Locale.getDefault())
				:
				forceTitle);
		ret.add(label);
		switch ((ShadowrunReference) choice.getChooseFrom()) {
		case ADEPT_POWER:
			ret.add( handleGeneric(item, choice, AdeptPower.class, null));
			break;
		case ATTRIBUTE:
			if (choice.getTypeReference()==null) {
				ret.add( handleATTRIBUTE(item, choice));
			} else {
				if (!"CHOICE".equals(choice.getTypeReference()))
					label.setText(ShadowrunAttribute.valueOf(choice.getTypeReference()).getName());
				ret.add( handleATTRIBUTEValues(item, choice));
			}
			break;
		case AMMUNITION_TYPE:
			ret.add( handleAMMUNITION_TYPE(item, choice) );
			break;
		case AUGMENTATION_QUALITY:
			ret.add( handleAUGMENTATIONQUALITY(item, choice));
			break;
		case CARRIED:
			ret.add( handleCARRIED(item, choice));
			break;
		case GEAR:
			ret.add( handleGEAR(item, choice));
			break;
		case ITEM_ATTRIBUTE:
			if (choice.getTypeReference()!=null) {
				SR6ItemAttribute attrib = SR6ItemAttribute.valueOf(choice.getTypeReference());
				ret.add( handleITEMATTRIBUTEValues(item, choice));
			} else {
				ret.add( handleITEMATTRIBUTE(item, choice));
			}
			break;
		case MATRIX_ATTRIBUTE:
			ret.add( handleITEMATTRIBUTE(item, choice, SR6ItemAttribute.matrixValues()));
			break;
		case MENTOR_SPIRIT:
			ret.add( handleMENTOR_SPIRIT(item, choice) );
			break;
		case PROGRAM:
			ret.add( handlePROGRAM(item, choice));
			break;
		case SKILL:
			ret.add( handleSKILL(item, choice) );
			break;
		case SPELL_CATEGORY:
			ret.add( handleSPELL_CATEGORY(item, choice) );
			break;
		case SPIRIT:
			ret.add( handleGeneric(item, choice, SR6NPC.class, npc -> npc.getType()==NPCType.SPIRIT));
			break;
		case SPRITE:
			ret.add( handleGeneric(item, choice, SR6NPC.class, npc -> npc.getType()==NPCType.SPRITE));
			break;
		case SUBSELECT:
			ret.add( handleSUBSELECT(item, choice) );
			break;
		case TEXT:
			ret.add( handleTEXT(item, choice) );
			break;
		default:
			System.err.println("Not implemented: choosing from " + choice.getChooseFrom());
			logger.log(Level.ERROR, "Not implemented: choosing from " + choice.getChooseFrom());
		}
		return ret;
	}

	//-------------------------------------------------------------------
	private Node handleSKILL(ComplexDataItem item, Choice choice) {
		ChoiceBox<SR6Skill> cbSub = new ChoiceBox<>();
		cbSub.setConverter(new StringConverter<SR6Skill>() {
			public SR6Skill fromString(String value) { return null;}
			public String toString(SR6Skill value) {
				if (value==null) return "-";
				return value.getName();
			}
		});
		// All but only given options?
		if (choice.getChoiceOptions()!=null) {
			List<String> ids = List.of(choice.getChoiceOptions());
			cbSub.getItems().addAll(
					Shadowrun6Core.getItemList(SR6Skill.class).stream().filter(s -> ids.contains(s.getId())).collect(Collectors.toList())
					);
		} else {
			cbSub.getItems().addAll(Shadowrun6Core.getItemList(SR6Skill.class));
		}
		Collections.sort(cbSub.getItems(), new Comparator<SR6Skill>() {
			public int compare(SR6Skill o1, SR6Skill o2) {
				return Collator.getInstance().compare(o1.getName(), o2.getName());
			}});
		cbSub.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.DEBUG, "Chose {0} for {1}", n, choice.getUUID());
			decisions.put(choice, new Decision(choice, n.getId()));
			updateButtons();
			showHelpFor(n); });
		content.getChildren().add(cbSub);
		return cbSub;
	}

	//-------------------------------------------------------------------
	private Node handleATTRIBUTE(ComplexDataItem item, Choice choice) {
		ChoiceBox<ShadowrunAttribute> cbSub = new ChoiceBox<>();
		cbSub.setConverter(new StringConverter<ShadowrunAttribute>() {
			public ShadowrunAttribute fromString(String value) { return null;}
			public String toString(ShadowrunAttribute value) {
				if (value==null) return "-";
				return value.getName();
			}
		});
		logger.log(Level.DEBUG, "handleATTRIBUTE: ref={0}  options={1}", choice.getTypeReference(), (choice.getChoiceOptions()!=null)?Arrays.toString(choice.getChoiceOptions()):"[]");
		// All but only given options?
		if (choice.getChoiceOptions()!=null) {
			List<String> ids = List.of(choice.getChoiceOptions());
			cbSub.getItems().addAll(
				List.of(ShadowrunAttribute.values()).stream().filter(s -> ids.contains(s.name())).collect(Collectors.toList())
				);
		} else {
			cbSub.getItems().addAll(ShadowrunAttribute.primaryValues());
		}

		Collections.sort(cbSub.getItems(), new Comparator<ShadowrunAttribute>() {
			public int compare(ShadowrunAttribute o1, ShadowrunAttribute o2) {
				return Collator.getInstance().compare(o1.getName(), o2.getName());
			}});
		cbSub.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.DEBUG, "Chose {0} for {1}", n, choice.getUUID());
			decisions.put(choice, new Decision(choice, n.name()));
			updateButtons();
		 });
		content.getChildren().add(cbSub);
		return cbSub;
	}

	//-------------------------------------------------------------------
	private Node handleATTRIBUTEValues(ComplexDataItem item, Choice choice) {
		ChoiceBox<String> cbSub = new ChoiceBox<>();
		cbSub.setConverter(new StringConverter<String>() {
			public String fromString(String value) { return null;}
			public String toString(String value) {
				if (value==null) return "-";
				try {
					return  ShadowrunAttribute.valueOf(value).getName() ;
				} catch (Exception e) {
				}
				return value;
			}
		});
		logger.log(Level.DEBUG, "handleATTRIBUTEValues: ref={0}  options={1}", choice.getTypeReference(), (choice.getChoiceOptions()!=null)?Arrays.toString(choice.getChoiceOptions()):"[]");
		cbSub.getItems().addAll( List.of(choice.getChoiceOptions()) );

		cbSub.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.DEBUG, "Chose {0} for {1}", n, choice.getUUID());
			decisions.put(choice, new Decision(choice, n));
			updateButtons();
		 });
		content.getChildren().add(cbSub);
		return cbSub;
	}

	//-------------------------------------------------------------------
	private Node handleITEMATTRIBUTE(ComplexDataItem item, Choice choice) {
		return handleITEMATTRIBUTE(item, choice, SR6ItemAttribute.values());
	}

	//-------------------------------------------------------------------
	private Node handleITEMATTRIBUTE(ComplexDataItem item, Choice choice, SR6ItemAttribute[] selectFrom) {
		ChoiceBox<SR6ItemAttribute> cbSub = new ChoiceBox<>();
		cbSub.setConverter(new StringConverter<SR6ItemAttribute>() {
			public SR6ItemAttribute fromString(String value) { return null;}
			public String toString(SR6ItemAttribute value) {
				if (value==null) return "-";
				return value.getName();
			}
		});
		// All but only given options?
		if (choice.getChoiceOptions()!=null) {
			List<String> ids = List.of(choice.getChoiceOptions());
			cbSub.getItems().addAll(
					List.of(SR6ItemAttribute.values()).stream().filter(s -> ids.contains(s.name())).collect(Collectors.toList())
					);
		} else {
			cbSub.getItems().addAll(selectFrom);
		}
		Collections.sort(cbSub.getItems(), new Comparator<SR6ItemAttribute>() {
			public int compare(SR6ItemAttribute o1, SR6ItemAttribute o2) {
				return Collator.getInstance().compare(o1.getName(), o2.getName());
			}});
		cbSub.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.DEBUG, "Chose {0} for {1}", n, choice.getUUID());
			decisions.put(choice, new Decision(choice, n.name()));
			updateButtons();
		 });
		content.getChildren().add(cbSub);
		return cbSub;
	}

	//-------------------------------------------------------------------
	private Node handleITEMATTRIBUTEValues(ComplexDataItem item, Choice choice) {
		ChoiceBox<String> cbSub = new ChoiceBox<>();
		// All but only given options?
		if (choice.getChoiceOptions()!=null) {
			List<String> ids = List.of(choice.getChoiceOptions());
			cbSub.getItems().addAll(List.of(choice.getChoiceOptions()));
		} else {
			throw new IllegalArgumentException("Only use this method with choice options");
		}
		cbSub.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.DEBUG, "Chose {0} for {1}", n, choice.getUUID());
			decisions.put(choice, new Decision(choice, n));
			updateButtons();
		 });
		content.getChildren().add(cbSub);
		return cbSub;
	}

	//-------------------------------------------------------------------
	private Node handleAUGMENTATIONQUALITY(ComplexDataItem item, Choice choice) {
		ChoiceBox<AugmentationQuality> cbSub = new ChoiceBox<>();
		cbSub.setConverter(new StringConverter<AugmentationQuality>() {
			public AugmentationQuality fromString(String value) { return null;}
			public String toString(AugmentationQuality value) {
				if (value==null) return "-";
				return value.getName();
			}
		});
		// All but only given options?
		if (choice.getChoiceOptions()!=null) {
			List<String> ids = List.of(choice.getChoiceOptions());
			cbSub.getItems().addAll(
					List.of(AugmentationQuality.values()).stream().filter(s -> ids.contains(s.name())).collect(Collectors.toList())
					);
		} else {
			cbSub.getItems().addAll(AugmentationQuality.values());
		}
		Collections.sort(cbSub.getItems(), new Comparator<AugmentationQuality>() {
			public int compare(AugmentationQuality o1, AugmentationQuality o2) {
				return Collator.getInstance().compare(o1.getName(), o2.getName());
			}});
		cbSub.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.DEBUG, "Chose {0} for {1}", n, choice.getUUID());
			decisions.put(choice, new Decision(choice, n.name()));
			updateButtons();
		 });
		content.getChildren().add(cbSub);
		return cbSub;
	}

	//-------------------------------------------------------------------
	private void populateAdeptChoices(MentorSpirit n) {
		for (Modification tmp: n.getAdeptModifications()) {
			DataItemModification mod = (DataItemModification)tmp;
			Object val = mod.getResolvedKey();
			if (val instanceof ComplexDataItem) {
				ComplexDataItem cplx = (ComplexDataItem) val;
				for (Choice choice : cplx.getChoices()) {
					logger.log(Level.INFO, "...... has choice "+choice);
					Choice cloned = (Choice) choice.clone();
					if (mod.getChoiceOptions()!=null && mod.getConnectedChoice()!=null && mod.getConnectedChoice().equals(choice.getUUID())) {
						logger.log(Level.DEBUG, "Restrict choice options from {0} to {1}", Arrays.toString(cloned.getChoiceOptions()), mod.getChoiceOptions());
						cloned.setChoiceOptions(mod.getChoiceOptions());
					}
					List<Node> added = processChoice(cplx, cloned, cplx.getName());
					added.forEach(node -> {
						node.visibleProperty().bind(chooseAdeptAdvantages.or(useBothAdvantages));
						node.managedProperty().bind(chooseAdeptAdvantages.or(useBothAdvantages));
					});
				}
			}
		}
	}

	//-------------------------------------------------------------------
	private void populateMagicianChoices(MentorSpirit n) {
		for (Modification tmp: n.getMagicianModifications()) {
			DataItemModification mod = (DataItemModification)tmp;
			Object val = mod.getResolvedKey();
			if (val instanceof ComplexDataItem) {
				ComplexDataItem cplx = (ComplexDataItem) val;
				for (Choice choice : cplx.getChoices()) {
					logger.log(Level.INFO, "...... has choice "+choice);
					List<Node> added = processChoice(cplx, choice, cplx.getName());
					added.forEach(node -> {
						node.visibleProperty().bind(chooseAdeptAdvantages.or(useBothAdvantages).not());
						node.managedProperty().bind(chooseAdeptAdvantages.or(useBothAdvantages).not());
					});
				}
			}
		}
	}

	//-------------------------------------------------------------------
	private <T extends DataItem> Node handleGeneric(ComplexDataItem item, Choice choice, Class<T> cls, Predicate<T> filter) {
		ChoiceBox<T> choicebox = new ChoiceBox<>();
		choicebox.setConverter(new StringConverter<T>() {
			public T fromString(String value) { return null;}
			public String toString(T value) {
				if (value==null) return "-";
				return value.getName();
			}
		});
		Collection<T> list = Shadowrun6Core.getItemList(cls);
		if (filter!=null) {
			list = list.stream().filter(filter).collect(Collectors.toList());
		}
		choicebox.getItems().addAll(list);
		Collections.sort(choicebox.getItems(), new Comparator<T>() {
			public int compare(T o1, T o2) {
				return Collator.getInstance().compare(o1.getName(), o2.getName());
			}});
		choicebox.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.DEBUG, "Chose {0} for {1}", n, choice.getUUID());
			decisions.put(choice, new Decision(choice, n.getId()));

			updateButtons();
			showHelpFor(n);
		 });
		content.getChildren().add(choicebox);

		return choicebox;
	}

	//-------------------------------------------------------------------
	private Node handleAMMUNITION_TYPE(ComplexDataItem item, Choice choice) {
		ChoiceBox<AmmunitionType> cbAmmoType = new ChoiceBox<>();
		cbAmmoType.setConverter(new StringConverter<AmmunitionType>() {
			public AmmunitionType fromString(String value) { return null;}
			public String toString(AmmunitionType value) {
				if (value==null) return "-";
				return value.getName();
			}
		});
		cbAmmoType.getItems().addAll(Shadowrun6Core.getItemList(AmmunitionType.class));
		Collections.sort(cbAmmoType.getItems(), new Comparator<AmmunitionType>() {
			public int compare(AmmunitionType o1, AmmunitionType o2) {
				return Collator.getInstance().compare(o1.getName(), o2.getName());
			}});
		cbAmmoType.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.DEBUG, "Chose {0} for {1}", n, choice.getUUID());
			decisions.put(choice, new Decision(choice, n.getId()));

			updateButtons();
			showHelpFor(n);
		 });
		content.getChildren().add(cbAmmoType);

		return cbAmmoType;
	}

	//-------------------------------------------------------------------
	private Node handleMENTOR_SPIRIT(ComplexDataItem item, Choice choice) {
		ChoiceBox<MentorSpirit> cbMentor = new ChoiceBox<>();
		cbMentor.setConverter(new StringConverter<MentorSpirit>() {
			public MentorSpirit fromString(String value) { return null;}
			public String toString(MentorSpirit value) {
				if (value==null) return "-";
				return value.getName();
			}
		});
		// All but only given options?
		if (choice.getChoiceOptions()!=null) {
			List<String> ids = List.of(choice.getChoiceOptions());
			cbMentor.getItems().addAll(
					Shadowrun6Core.getItemList(MentorSpirit.class).stream().filter(s -> ids.contains(s.getId())).collect(Collectors.toList())
					);
		} else {
			cbMentor.getItems().addAll(Shadowrun6Core.getItemList(MentorSpirit.class));
		}
		Collections.sort(cbMentor.getItems(), new Comparator<MentorSpirit>() {
			public int compare(MentorSpirit o1, MentorSpirit o2) {
				return Collator.getInstance().compare(o1.getName(), o2.getName());
			}});
		cbMentor.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.DEBUG, "Chose {0} for {1}", n, choice.getUUID());
			decisions.put(choice, new Decision(choice, n.getId()));

			// Clear content from previous mentor spirit selection
			if (o!=null && !o.getChoices().isEmpty()) {
				choices.removeAll(o.getChoices());
				o.getChoices().forEach(c -> decisions.remove(o));
			}
			content.getChildren().removeAll(toDeleteOnMentorSpirit);

			// Memorize current GUI elements
			List<Node> current = new ArrayList<Node>(content.getChildren());

			// Populate with generic decisions
			logger.log(Level.INFO, "Choices of Mentor spirit: "+n.getChoices());
			for (Choice tmpChoice : n.getChoices()) {
				processChoice(item, tmpChoice, null);
				choices.add(choice);
			}
			populateMagicianChoices(n);
			populateAdeptChoices(n);

			// Compare with now updated
			List<Node> updated = new ArrayList<Node>(content.getChildren());
			toDeleteOnMentorSpirit  = updated.stream().filter(i -> !current.contains(i)).collect(Collectors.toList());


			updateButtons();
			showHelpFor(n);
		 });
		content.getChildren().add(cbMentor);

		Shadowrun6Character model = ctrl.getModel();
		if (model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesPowers() && model.getMagicOrResonanceType().usesSpells()) {
			addLabel(ResourceI18N.get(RES, "choice.magician_adept"));

			ChoiceBox<MagicOrResonanceType> cbMagOrAdp = new ChoiceBox<>();
			cbMagOrAdp.getItems().add(Shadowrun6Core.getItem(MagicOrResonanceType.class, "magician"));
			cbMagOrAdp.getItems().add(Shadowrun6Core.getItem(MagicOrResonanceType.class, "adept"));
			cbMagOrAdp.setConverter(new StringConverter<MagicOrResonanceType>() {
				public String toString(MagicOrResonanceType v) {return (v!=null)?v.getName():null;}
				public MagicOrResonanceType fromString(String string) {return null;}
			});
			content.getChildren().add(cbMagOrAdp);
			cbMagOrAdp.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
				logger.log(Level.DEBUG, "Use advantages for "+n);
				decisions.put(magicianOrAdept, new Decision(magicianOrAdept, n.getId()));
				chooseAdeptAdvantages.setValue (n!=null && n.usesPowers());
			});
		}

		return cbMentor;
	}

	//-------------------------------------------------------------------
	private Node handleSUBSELECT(ComplexDataItem item, Choice choice) {
		ChoiceBox<ChoiceOption> cbSub = new ChoiceBox<>();
		cbSub.setConverter(new StringConverter<ChoiceOption>() {
			public ChoiceOption fromString(String value) { return null;}
			public String toString(ChoiceOption value) {
				if (value==null) return "-";
				String name = item.getChoiceOptionStrings(choice, value, Locale.getDefault())[0];
				if (value.getCost()!=0) {
					if (item instanceof ItemTemplate) {
						if ( Math.round(value.getCost())==value.getCost()) {
							name+=" (+"+((int)(value.getCost()))+" Nuyen)";
						} else {
							name+=" ("+value.getCost()+" Nuyen)";
						}
					} else {
						if ( Math.round(value.getCost())==value.getCost()) {
							name+=" (+"+((int)(value.getCost()))+" Karma)";
						} else {
							name+=" ("+value.getCost()+" Karma)";
						}
					}
				}
				return name;
			}
		});
		for (ChoiceOption opt : choice.getSubOptions()) {
			logger.log(Level.DEBUG, "  sub option "+opt);
			cbSub.getItems().add(opt);

		}
		cbSub.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.DEBUG, "Chose {0} for {1}", n, choice.getUUID());
			decisions.put(choice, new Decision(choice, n.getId()));
			updateButtons();
			showHelpFor(n); });
		content.getChildren().add(cbSub);
		return cbSub;
	}

	//-------------------------------------------------------------------
	private Node handleTEXT(ComplexDataItem item, Choice choice) {
		TextField tfDescr = new TextField();
		content.getChildren().add(tfDescr);
		tfDescr.textProperty().addListener( (ov,o,n) -> {
			logger.log(Level.DEBUG, "Chose {0} for {1}", n, choice.getUUID());
			decisions.put(choice, new Decision(choice, n));
			updateButtons();
		});
		return tfDescr;
	}

	//-------------------------------------------------------------------
	private Node handleGEAR(ComplexDataItem item, Choice choice) {
		ChoiceBox<ItemTemplate> choicebox = new ChoiceBox<>();
		choicebox.setConverter(new StringConverter<ItemTemplate>() {
			public ItemTemplate fromString(String value) { return null;}
			public String toString(ItemTemplate value) {
				if (value==null) return "-";
				return value.getName();
			}
		});
		List<ItemTemplate> items = new ArrayList<>();
		// If options are given, use those
		if (choice.getChoiceOptions()!=null && choice.getChoiceOptions().length>0) {
			List<ItemTemplate> overwrite = new ArrayList<>();
			for (String key : choice.getChoiceOptions()) {
				ItemTemplate resolved = Shadowrun6Core.getItem(ItemTemplate.class, key);
				if (resolved==null)
					throw new NullPointerException("No such gear '"+key+"'");
				overwrite.add(resolved);
			}
			items.clear();
			items.addAll(overwrite);
		}

		// Eventually sort
		if (choice.getTypeReference()!=null) {
			logger.log(Level.DEBUG, "Reduce gear to select from to "+choice.getTypeReference());
			System.err.println("ChoiceSelectorDialog: Reduce gear to select from to "+choice.getTypeReference());
			switch (choice.getTypeReference()) {
			case "MELEE":
				items = items.stream().filter(i -> i.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue()==ItemType.WEAPON_CLOSE_COMBAT).collect(Collectors.toList());
				break;
			case "CHEMICALS":
				items = Shadowrun6Core.getItemList(ItemTemplate.class);
				items = items.stream().filter(i -> i.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue()==ItemType.CHEMICALS && i.getAttribute(SR6ItemAttribute.ITEMSUBTYPE).getValue()==ItemSubType.INDUSTRIAL_CHEMICALS).collect(Collectors.toList());
				break;
			case "PARENT_OR_ALTERNATES":
				logger.log(Level.WARNING, "TODO: Check this for item detection");
				System.err.println("ChoiceSelectorDialog: TODO: Check this for item detection");
				if (context!=null && context.getResolved() instanceof ItemTemplate) {
					ItemTemplate temp = (ItemTemplate)context.getResolved();
					items.add(temp);
					items.addAll(temp.getGearSubDefinitions());
				}
				break;
			default:
				logger.log(Level.WARNING, "Don't know how to reduce GEAR to '"+choice.getTypeReference()+"'");
			}
		}
		choicebox.getItems().addAll(items);

		Collections.sort(choicebox.getItems(), new Comparator<ItemTemplate>() {
			public int compare(ItemTemplate o1, ItemTemplate o2) {
				return Collator.getInstance().compare(o1.getName(), o2.getName());
			}});
		choicebox.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.WARNING, "Chose {0} for {1}", n, choice.getUUID());
			decisions.put(choice, new Decision(choice, n.getId()));

			updateButtons();
			showHelpFor(n);
		 });
		content.getChildren().add(choicebox);

		return choicebox;
	}

	//-------------------------------------------------------------------
	private Node handleCARRIED(ComplexDataItem item, Choice choice) {
		ChoiceBox<CarriedItem<ItemTemplate>> choicebox = new ChoiceBox<>();
		choicebox.setConverter(new StringConverter<CarriedItem<ItemTemplate>>() {
			public CarriedItem<ItemTemplate> fromString(String value) { return null;}
			public String toString(CarriedItem<ItemTemplate> value) {
				if (value==null) return "-";
				return value.getNameWithRating();
			}
		});
		List<CarriedItem<ItemTemplate>> items = ((Shadowrun6Character)ctrl.getModel()).getCarriedItems();

		// Eventually sort
		if (choice.getTypeReference()!=null) {
			logger.log(Level.DEBUG, "Reduce gear to select from to "+choice.getTypeReference());
			System.err.println("Reduce gear to select from to "+choice.getTypeReference());
			switch (choice.getTypeReference()) {
			case "MELEE":
				items = items.stream().filter(i -> i.getAsObject(SR6ItemAttribute.ITEMTYPE).getValue()==ItemType.WEAPON_CLOSE_COMBAT).collect(Collectors.toList());
				break;
			default:
				logger.log(Level.WARNING, "Don't know how to reduce GEAR to '"+choice.getTypeReference()+"'");
			}
		}
		choicebox.getItems().addAll(items);

		Collections.sort(choicebox.getItems(), new Comparator<CarriedItem<ItemTemplate>>() {
			public int compare(CarriedItem<ItemTemplate> o1, CarriedItem<ItemTemplate> o2) {
				return Collator.getInstance().compare(o1.getNameWithRating(), o2.getNameWithRating());
			}});
		choicebox.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.DEBUG, "Chose {0} for {1}", n, choice.getUUID());
			decisions.put(choice, new Decision(choice, n.getUuid().toString()));

			updateButtons();
			showHelpFor(n);
		 });
		content.getChildren().add(choicebox);

		return choicebox;
	}

	//-------------------------------------------------------------------
	private Node handlePROGRAM(ComplexDataItem item, Choice choice) {
		ChoiceBox<ItemTemplate> choicebox = new ChoiceBox<>();
		choicebox.setConverter(new StringConverter<ItemTemplate>() {
			public ItemTemplate fromString(String value) { return null;}
			public String toString(ItemTemplate value) {
				if (value==null) return "-";
				return value.getName();
			}
		});
		List<ItemTemplate> items = Shadowrun6Core.getItemList(ItemTemplate.class)
				.stream()
				.filter(new ItemTypeFilter(CarryMode.EMBEDDED, ItemType.SOFTWARE))
				.toList();
		// Eventually sort
		choicebox.getItems().addAll(items);

		Collections.sort(choicebox.getItems(), new Comparator<ItemTemplate>() {
			public int compare(ItemTemplate o1, ItemTemplate o2) {
				return Collator.getInstance().compare(o1.getName(), o2.getName());
			}});
		choicebox.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.DEBUG, "Chose {0} for {1}", n, choice.getUUID());
			decisions.put(choice, new Decision(choice, n.getId()));

			updateButtons();
			showHelpFor(n);
		 });
		content.getChildren().add(choicebox);

		return choicebox;
	}

	//-------------------------------------------------------------------
	private Node handleSPELL_CATEGORY(ComplexDataItem item, Choice choice) {
		ChoiceBox<ASpell.Category> cbSub = new ChoiceBox<>();
		cbSub.setConverter(new StringConverter<ASpell.Category>() {
			public ASpell.Category fromString(String value) { return null;}
			public String toString(ASpell.Category value) {
				if (value==null) return "-";
				return value.getName();
			}
		});
		// All but only given options?
		if (choice.getChoiceOptions()!=null) {
			List<String> ids = List.of(choice.getChoiceOptions());
			cbSub.getItems().addAll(
					List.of(ASpell.Category.values()).stream().filter(s -> ids.contains(s.name())).collect(Collectors.toList())
					);
		} else {
			cbSub.getItems().addAll(ASpell.Category.values());
		}
		Collections.sort(cbSub.getItems(), new Comparator<ASpell.Category>() {
			public int compare(ASpell.Category o1, ASpell.Category o2) {
				return Collator.getInstance().compare(o1.getName(), o2.getName());
			}});
		cbSub.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.DEBUG, "Chose {0} for {1}", n, choice.getUUID());
			decisions.put(choice, new Decision(choice, n.name()));
			updateButtons();
		 });
		content.getChildren().add(cbSub);
		return cbSub;
	}

}
