package de.rpgframework.shadowrun6.chargen.jfx;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import de.rpgframework.HasName;
import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.ChoiceOption;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.DataItemValue;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.PieceOfGear;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModificationChoice;
import de.rpgframework.jfx.pane.DecisionDialog.DecisionDialogConfig;
import de.rpgframework.shadowrun.ASpell;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.DamageElement;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.MentorSpirit;
import de.rpgframework.shadowrun.NPCType;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.ShadowrunElement;
import de.rpgframework.shadowrun.chargen.jfx.dialog.ShadowrunDecisionDialogStorage;
import de.rpgframework.shadowrun.items.AugmentationQuality;
import de.rpgframework.shadowrun6.SR6NPC;
import de.rpgframework.shadowrun6.SR6Quality;
import de.rpgframework.shadowrun6.SR6RuleFlag;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.Sense;
import de.rpgframework.shadowrun6.Shadowrun6Action;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.SignatureManeuver;
import de.rpgframework.shadowrun6.items.AmmunitionType;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemTypeFilter;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * Creates the input nodes for the choices of an item in the generic
 * {@link de.rpgframework.jfx.pane.DecisionDialog}.
 * Extends {@link ShadowrunDecisionDialogStorage} for Shadowrun 6 references.
 * <p>
 * Holds the state of one dialog - use a new instance per dialog.
 *
 * @author prelle
 */
public class SR6DecisionDialogStorage extends ShadowrunDecisionDialogStorage {

	/** Optional: The item the decisions are made for, e.g. the container to embed into */
	private DataItemValue<?> context;

	public DataItemValue<?> getContext() {
		return context;
	}

	public void setContext(DataItemValue<?> context) {
		this.context = context;
	}

	private final static Logger logger = System.getLogger(SR6DecisionDialogStorage.class.getPackageName());

	private final static ResourceBundle RES = SR6CharacterViewLayout.UI;

	/** Decides the variant of a piece of gear */
	public final static Choice VARIANT_CHOICE = new Choice(PieceOfGear.VARIANT, null);
	/** Decides the rating of an item with levels, that has no own rating choice */
	public final static Choice RATING_CHOICE = new Choice(ItemTemplate.UUID_RATING, null);
	/** Mystic adepts: use the advantages for magicians or for adepts */
	public final static Choice MENTOR_ADVANTAGES_CHOICE = new Choice(QualityValue.MENTOR_SPIRIT_ADVANTAGES, ShadowrunReference.MAGIC_RESO);

	// ---
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.dialog.ShadowrunDecisionDialogStorage#createChoiceNode(de.rpgframework.genericrpg.data.Choice)
	 */
	@Override
	protected Node createChoiceNode(Choice choice) {
		ShadowrunReference type = (ShadowrunReference) choice.getChooseFrom();
		if (type == null) {
			return notSupported(null);
		}
		switch (type) {
		case ACTION:
			return handleDataItems(choice, Shadowrun6Core.getItemList(Shadowrun6Action.class));
		case ADEPT_POWER:
			return handleDataItems(choice, Shadowrun6Core.getItemList(AdeptPower.class));
		case AMMUNITION_TYPE:
			return handleDataItems(choice, Shadowrun6Core.getItemList(AmmunitionType.class));
		case ATTRIBUTE:
			if (choice.getTypeReference() == null || "CHOICE".equals(choice.getTypeReference())) {
				return handleAttribute(choice, ShadowrunAttribute.primaryValues());
			}
			return handleOptions(choice, false);
		case AUGMENTATION_QUALITY:
			return handleChoiceBox(choice, restrictToOptions(choice, List.of(AugmentationQuality.values()), Enum::name),
					AugmentationQuality::getName, Enum::name);
		case CARRIED:
			return handleCarried(choice);
		case DAMAGE_ELEMENT:
			return handleNamedEnum(choice, DamageElement.class);
		case ELEMENT:
			return handleNamedEnum(choice, ShadowrunElement.class);
		case GEAR:
			return handleGear(choice);
		case ITEM_ATTRIBUTE:
			if (choice.getChoiceOptions() != null) {
				return handleOptions(choice, false);
			} else if (choice.getTypeReference() != null && choice.getMaxFormula() != null) {
				return handleRange(choice, 1, choice.getMaxFormula().getAsInteger(), false);
			} else {
				return handleChoiceBox(choice, List.of(SR6ItemAttribute.values()), SR6ItemAttribute::getName, Enum::name);
			}
		case MATRIX_ATTRIBUTE:
			return handleChoiceBox(choice, List.of(SR6ItemAttribute.matrixValues()), SR6ItemAttribute::getName, Enum::name);
		case MENTOR_SPIRIT:
			return handleMentorSpirit(choice, MentorSpirit.Type.MENTOR_SPIRIT);
		case PARAGON:
			return handleMentorSpirit(choice, MentorSpirit.Type.PARAGON);
		case PROGRAM:
			return handleDataItems(choice, Shadowrun6Core.getItemList(ItemTemplate.class).stream()
					.filter(new ItemTypeFilter(CarryMode.EMBEDDED, ItemType.SOFTWARE))
					.toList());
		case QUALITY:
			return handleDataItems(choice, Shadowrun6Core.getItemList(SR6Quality.class));
		case SENSE:
			return handleDataItems(choice, Shadowrun6Core.getItemList(Sense.class));
		case SIGNATURE_MANEUVERS:
			return handleSignatureManeuvers(choice);
		case SKILL:
			return handleChoiceBox(choice, resolveSkills(choice), SR6Skill::getName, SR6Skill::getId);
		case SKILLSPECIALIZATION:
			return handleChoiceBox(choice, resolveSpecializations(choice),
					SkillSpecialization::getName, spec -> spec.getSkill().getId() + "/" + spec.getId());
		case SPELL:
			return handleDataItems(choice, Shadowrun6Core.getItemList(SR6Spell.class));
		case SPELL_CATEGORY:
			return handleChoiceBox(choice, restrictToOptions(choice, List.of(ASpell.Category.values()), Enum::name),
					ASpell.Category::getName, Enum::name);
		case SPIRIT:
			return handleDataItems(choice, Shadowrun6Core.getItemList(SR6NPC.class).stream()
					.filter(npc -> npc.getType() == NPCType.SPIRIT)
					.toList());
		case SPRITE:
			return handleDataItems(choice, Shadowrun6Core.getItemList(SR6NPC.class).stream()
					.filter(npc -> npc.getType() == NPCType.SPRITE)
					.toList());
		case SUBSELECT:
			return handleSubSelect(choice);
		case TEXT:
			return handleText(choice);
		default:
			if (type.getEnumType() != null) {
				return handleEnum(choice, type.getEnumType());
			}
			return notSupported(type);
		}
	}

	// ===================================================================
	// Variant and rating
	// ===================================================================

	// ---
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.dialog.ShadowrunDecisionDialogStorage#createItemNodes()
	 */
	@Override
	protected List<Node> createItemNodes() {
		List<Node> ret = new ArrayList<>();
		Object item = (getConfig() != null) ? getConfig().getItem() : null;
		if (item instanceof ItemTemplate tmpl && !tmpl.getVariants().isEmpty()) {
			ret.addAll(createVariantNodes(tmpl));
		}
		if (item instanceof ComplexDataItem cdi && cdi.hasLevel() && cdi.getChoice(ItemTemplate.UUID_RATING) == null) {
			int max = (cdi instanceof Quality q && q.getMax() > 0) ? q.getMax() : 10;
			ret.add(heading(ResourceI18N.get(RES, "decision.label.rating")));
			ret.add(handleRange(RATING_CHOICE, 1, max, true));
		}
		return ret;
	}

	// ---
	@SuppressWarnings("unchecked")
	private List<Node> createVariantNodes(ItemTemplate tmpl) {
		List<Node> ret = new ArrayList<>();
		ret.add(heading(ResourceI18N.get(RES, "decision.label.variant")));
		ChoiceBox<SR6PieceOfGearVariant> cbVariants = new ChoiceBox<>();
		if (!tmpl.requiresVariant()) {
			cbVariants.getItems().add(null);
		}
		cbVariants.getItems().addAll((java.util.Collection<SR6PieceOfGearVariant>) (java.util.Collection<?>) tmpl.getVariants());
		cbVariants.setConverter(new StringConverter<SR6PieceOfGearVariant>() {
			public SR6PieceOfGearVariant fromString(String value) { return null; }
			public String toString(SR6PieceOfGearVariant value) { return variantName(tmpl, value); }
		});
		ret.add(cbVariants);

		// Choices that only exist in a variant
		Map<SR6PieceOfGearVariant, VBox> perVariant = new LinkedHashMap<>();
		for (SR6PieceOfGearVariant variant : cbVariants.getItems()) {
			if (variant == null || variant.getChoices() == null || variant.getChoices().isEmpty()) continue;
			VBox box = new VBox(5);
			for (Choice choice : variant.getChoices()) {
				box.getChildren().add(heading(tmpl.getChoiceName(choice, Locale.getDefault())));
				box.getChildren().add(createChoiceNode(choice));
			}
			box.setVisible(false);
			box.setManaged(false);
			perVariant.put(variant, box);
			ret.add(box);
		}

		cbVariants.getSelectionModel().selectedItemProperty().addListener((_, o, n) -> {
			logger.log(Level.DEBUG, "Chose variant {0}", n);
			getConfig().setVariant((n != null) ? n.getId() : null);
			if (o != null && perVariant.containsKey(o)) {
				perVariant.get(o).setVisible(false);
				perVariant.get(o).setManaged(false);
				Set<UUID> old = new HashSet<>();
				o.getChoices().forEach(c -> old.add(c.getUUID()));
				getDecisions().keySet().removeIf(c -> old.contains(c.getUUID()));
			}
			if (n != null && perVariant.containsKey(n)) {
				perVariant.get(n).setVisible(true);
				perVariant.get(n).setManaged(true);
			}
			decide(VARIANT_CHOICE, (n != null) ? n.getId() : null);
		});

		// Preselect suggested or already chosen variant
		String suggested = (getConfig().getVariant() != null) ? getConfig().getVariant() : currentDecision(VARIANT_CHOICE);
		cbVariants.getItems().stream().filter(v -> v != null && v.getId().equals(suggested)).findFirst()
			.ifPresentOrElse(v -> cbVariants.getSelectionModel().select(v), () -> cbVariants.getSelectionModel().selectFirst());
		return ret;
	}

	// ---
	private static String variantName(ItemTemplate tmpl, SR6PieceOfGearVariant value) {
		if (value == null) return ResourceI18N.get(RES, "decision.variant.regular");
		String name = tmpl.getVariantName(value, Locale.getDefault());
		if (name.startsWith(tmpl.getTypeString())) {
			ItemSubType sub = (tmpl.getAttribute(SR6ItemAttribute.ITEMSUBTYPE) != null)
					? tmpl.getAttribute(SR6ItemAttribute.ITEMSUBTYPE).getValue() : null;
			name = (sub == null)
					? tmpl.getLocalizedString(Locale.getDefault(), "variant." + value.getId())
					: tmpl.getLocalizedString(Locale.getDefault(), "variant." + sub.name().toLowerCase() + "." + value.getId());
		}
		return name;
	}

	// ===================================================================
	// Choice types
	// ===================================================================

	// ---
	private List<SR6Skill> resolveSkills(Choice choice) {
		return (choice.getChoiceOptions() != null)
				? Arrays.stream(choice.getChoiceOptions()).map(Shadowrun6Core::getSkill).filter(Objects::nonNull).toList()
				: Shadowrun6Core.getItemList(SR6Skill.class);
	}

	// ---
	@SuppressWarnings("unchecked")
	private List<SkillSpecialization<SR6Skill>> resolveSpecializations(Choice choice) {
		List<SkillSpecialization<SR6Skill>> ret = new ArrayList<>();
		if (choice.getChoiceOptions() != null) {
			for (String fullID : choice.getChoiceOptions()) {
				String[] split = fullID.split("/");
				SR6Skill skill = Shadowrun6Core.getSkill(split[0]);
				SkillSpecialization<SR6Skill> spec = (skill != null && split.length > 1) ? skill.getSpecialization(split[1]) : null;
				if (spec == null) {
					logger.log(Level.ERROR, "Choice {0} references unknown specialization ''{1}''", choice.getUUID(), fullID);
				} else {
					ret.add(spec);
				}
			}
		} else if (choice.getTypeReference() != null) {
			SR6Skill skill = Shadowrun6Core.getSkill(choice.getTypeReference());
			if (skill != null) {
				skill.getSpecializations().forEach(spec -> ret.add((SkillSpecialization<SR6Skill>) spec));
			}
		}
		return ret;
	}

	// ---
	private <X extends Enum<?> & HasName> ChoiceBox<X> handleNamedEnum(Choice choice, Class<X> enumType) {
		List<X> values = new ArrayList<>(List.of(enumType.getEnumConstants()));
		if (choice.getChoiceOptions() != null) {
			List<String> valid = List.of(choice.getChoiceOptions());
			values.removeIf(v -> !valid.contains(v.getId().toUpperCase()));
		}
		ComplexDataItem parent = (getConfig() != null && getConfig().getItem() instanceof ComplexDataItem cdi) ? cdi : null;
		return handleChoiceBox(choice, values, value -> {
			if (parent != null) {
				String key = parent.getChoiceOptionString(choice, value, Locale.getDefault());
				if (key != null && !key.contains("choice"))
					return key;
			}
			return value.getName(Locale.getDefault());
		}, Enum::name);
	}

	// ---
	private Node handleSubSelect(Choice choice) {
		ComplexDataItem item = (getConfig() != null && getConfig().getItem() instanceof ComplexDataItem cdi) ? cdi : null;
		String unit = (item instanceof ItemTemplate) ? " Nuyen" : " Karma";
		ChoiceBox<ChoiceOption> cbSub = handleChoiceBox(choice, choice.getSubOptions(), value -> {
			String name = (item != null) ? item.getChoiceOptionStrings(choice, value, Locale.getDefault())[0] : value.getId();
			if (value.getCost() != 0) {
				name += (Math.round(value.getCost()) == value.getCost())
						? " (+" + ((int) value.getCost()) + unit + ")"
						: " (" + value.getCost() + unit + ")";
			}
			return name;
		}, ChoiceOption::getId);
		cbSub.getItems().setAll(choice.getSubOptions());
		String current = currentDecision(choice);
		cbSub.getItems().stream().filter(v -> v.getId().equals(current)).findFirst()
			.ifPresent(v -> cbSub.getSelectionModel().select(v));
		return cbSub;
	}

	// ---
	private Node handleSignatureManeuvers(Choice choice) {
		Shadowrun6Character model = model();
		List<SignatureManeuver> items = (model != null) ? model.getSignatureManeuvers() : List.of();
		return handleChoiceBox(choice, items, SignatureManeuver::getName, m -> m.getUuid().toString());
	}

	// ---
	private Node handleGear(Choice choice) {
		List<ItemTemplate> items;
		if (choice.getChoiceOptions() != null && choice.getChoiceOptions().length > 0) {
			items = new ArrayList<>();
			for (String key : choice.getChoiceOptions()) {
				ItemTemplate resolved = Shadowrun6Core.getItem(ItemTemplate.class, key);
				if (resolved == null) {
					logger.log(Level.ERROR, "Choice {0} references unknown gear ''{1}''", choice.getUUID(), key);
				} else {
					items.add(resolved);
				}
			}
		} else {
			ComplexDataItemController<?, ?> ctrl = (getDialog() != null) ? getDialog().getController() : null;
			items = Shadowrun6Core.getItemList(ItemTemplate.class).stream()
					.filter(data -> ctrl == null || ctrl.getCharacterController().showDataItem(data))
					.toList();
		}
		Shadowrun6Character model = model();
		List<CarriedItem<ItemTemplate>> carried = (model != null) ? model.getCarriedItemsRecursive() : List.of();
		Predicate<ItemTemplate> isCarried = tmpl -> carried.stream().anyMatch(ci -> ci.getResolved() == tmpl);
		if (choice.getTypeReference() != null) {
			switch (choice.getTypeReference()) {
			case "MELEE":
				items = items.stream().filter(i -> i.getAttribute(SR6ItemAttribute.ITEMTYPE) != null
						&& i.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue() == ItemType.WEAPON_CLOSE_COMBAT).toList();
				break;
			case "CHEMICALS":
				items = Shadowrun6Core.getItemList(ItemTemplate.class).stream()
						.filter(i -> i.getAttribute(SR6ItemAttribute.ITEMTYPE) != null
								&& i.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue() == ItemType.CHEMICALS
								&& i.getAttribute(SR6ItemAttribute.ITEMSUBTYPE) != null
								&& i.getAttribute(SR6ItemAttribute.ITEMSUBTYPE).getValue() == ItemSubType.INDUSTRIAL_CHEMICALS)
						.toList();
				break;
			case "PARENT_OR_ALTERNATES":
				items = new ArrayList<>(items);
				if (context != null && context.getResolved() instanceof ItemTemplate temp) {
					items.add(temp);
					items.addAll(temp.getGearSubDefinitions());
				}
				break;
			case "VEHICLE":
				items = items.stream().filter(i -> i.getAttribute(SR6ItemAttribute.ITEMTYPE) != null
						&& ItemType.isVehicle((ItemType) i.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue())).filter(isCarried).toList();
				break;
			case "WEAPON":
				items = items.stream().filter(i -> i.getAttribute(SR6ItemAttribute.ITEMTYPE) != null
						&& ItemType.isWeapon((ItemType) i.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue())).filter(isCarried).toList();
				break;
			default:
				logger.log(Level.ERROR, "Don''t know how to reduce GEAR to ''{0}''", choice.getTypeReference());
			}
		}
		if (items.isEmpty()) {
			logger.log(Level.WARNING, "No gear to select from for choice {0} ({1})", choice.getUUID(), choice.getTypeReference());
		}
		return handleChoiceBox(choice, items, ItemTemplate::getName, ItemTemplate::getId);
	}

	// ---
	private Node handleCarried(Choice choice) {
		Shadowrun6Character model = model();
		List<CarriedItem<ItemTemplate>> items = (model != null) ? model.getCarriedItems() : List.of();
		if ("MELEE".equals(choice.getTypeReference())) {
			items = items.stream().filter(i -> i.getAsObject(SR6ItemAttribute.ITEMTYPE) != null
					&& i.getAsObject(SR6ItemAttribute.ITEMTYPE).getValue() == ItemType.WEAPON_CLOSE_COMBAT).toList();
		} else if (choice.getTypeReference() != null) {
			logger.log(Level.WARNING, "Don''t know how to reduce CARRIED to ''{0}''", choice.getTypeReference());
		}
		return handleChoiceBox(choice, items, CarriedItem::getNameWithRating, ci -> ci.getUuid().toString());
	}

	// ===================================================================
	// Mentor spirit
	// ===================================================================

	// ---
	private Node handleMentorSpirit(Choice choice, MentorSpirit.Type type) {
		ComplexDataItemController<?, ?> ctrl = (getDialog() != null) ? getDialog().getController() : null;
		List<MentorSpirit> all = Shadowrun6Core.getItemList(MentorSpirit.class).stream()
				.filter(s -> s.getType() == type)
				.filter(Shadowrun6Tools.filterByLanguage(Locale.getDefault()))
				.filter(s -> choice.getChoiceOptions() == null || ctrl == null || ctrl.getCharacterController().showDataItem(s))
				.toList();
		ChoiceBox<MentorSpirit> cbMentor = handleChoiceBox(choice, restrictToOptions(choice, all, DataItem::getId),
				MentorSpirit::getName, MentorSpirit::getId);
		VBox ret = new VBox(5, cbMentor);

		Shadowrun6Character model = model();
		MagicOrResonanceType mor = (model != null) ? model.getMagicOrResonanceType() : null;
		BooleanProperty adeptAdvantages = new SimpleBooleanProperty(mor != null && mor.usesPowers());
		BooleanProperty bothAdvantages  = new SimpleBooleanProperty(model != null && model.hasRuleFlag(SR6RuleFlag.MENTOR_SPIRIT_BOTH));
		if (mor != null && mor.usesPowers() && mor.usesSpells()) {
			ret.getChildren().add(heading(ResourceI18N.get(RES, "decision.choice.magician_adept")));
			ChoiceBox<MagicOrResonanceType> cbMagOrAdp = handleChoiceBox(MENTOR_ADVANTAGES_CHOICE,
					List.of(Shadowrun6Core.getItem(MagicOrResonanceType.class, "magician"), Shadowrun6Core.getItem(MagicOrResonanceType.class, "adept")),
					MagicOrResonanceType::getName, MagicOrResonanceType::getId);
			cbMagOrAdp.getSelectionModel().selectedItemProperty().addListener((_, _, n) -> adeptAdvantages.set(n != null && n.usesPowers()));
			if (cbMagOrAdp.getSelectionModel().isEmpty()) {
				cbMagOrAdp.getSelectionModel().selectFirst();
			}
			adeptAdvantages.set(cbMagOrAdp.getSelectionModel().getSelectedItem() != null && cbMagOrAdp.getSelectionModel().getSelectedItem().usesPowers());
			ret.getChildren().add(cbMagOrAdp);
		}

		VBox extras = new VBox(5);
		ret.getChildren().add(extras);
		Set<UUID> mentorChoices = new HashSet<>();
		cbMentor.getSelectionModel().selectedItemProperty().addListener((_, _, n) -> {
			getDecisions().keySet().removeIf(c -> mentorChoices.contains(c.getUUID()));
			mentorChoices.clear();
			extras.getChildren().clear();
			if (n != null) {
				for (Choice sub : n.getChoices()) {
					mentorChoices.add(sub.getUUID());
					extras.getChildren().addAll(heading(n.getChoiceName(sub, Locale.getDefault())), createChoiceNode(sub));
				}
				addAdvantageChoices(extras, n.getMagicianModifications(), "decision.choice.magician", mentorChoices, adeptAdvantages.not().or(bothAdvantages));
				addAdvantageChoices(extras, n.getAdeptModifications(), "decision.choice.adept", mentorChoices, adeptAdvantages.or(bothAdvantages));
			}
			notifyChanged(choice);
		});
		MentorSpirit preselected = cbMentor.getSelectionModel().getSelectedItem();
		if (preselected != null) {
			cbMentor.getSelectionModel().clearSelection();
			cbMentor.getSelectionModel().select(preselected);
		}
		return ret;
	}

	// ---
	private void addAdvantageChoices(VBox extras, List<Modification> mods, String titleKey, Set<UUID> remember,
			javafx.beans.value.ObservableBooleanValue visible) {
		for (Modification tmp : mods) {
			List<Node> added = new ArrayList<>();
			if (tmp instanceof ModificationChoice mc) {
				remember.add(mc.getUUID());
				added.add(heading(ResourceI18N.get(RES, titleKey)));
				added.add(handleModificationChoice(mc, m -> Shadowrun6Tools.getModificationString(m, Locale.getDefault())));
			} else if (tmp instanceof DataItemModification mod && mod.getResolvedKey() instanceof ComplexDataItem cplx) {
				for (Choice sub : cplx.getChoices()) {
					Choice cloned = (Choice) sub.clone();
					if (mod.getChoiceOptions() != null && sub.getUUID().equals(mod.getConnectedChoice())) {
						logger.log(Level.DEBUG, "Restrict choice options from {0} to {1}", Arrays.toString(cloned.getChoiceOptions()), mod.getChoiceOptions());
						cloned.setChoiceOptions(mod.getChoiceOptions());
					}
					remember.add(cloned.getUUID());
					added.add(heading(cplx.getName()));
					added.add(createChoiceNode(cloned));
				}
			}
			added.forEach(node -> {
				node.visibleProperty().bind(visible);
				node.managedProperty().bind(visible);
			});
			extras.getChildren().addAll(added);
		}
	}

	// ---
	private Shadowrun6Character model() {
		return (getConfig() != null && getConfig().getModel() instanceof Shadowrun6Character model) ? model : null;
	}

}
