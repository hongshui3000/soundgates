package soundgates.diagram.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.gef.Request;
import org.eclipse.gef.Tool;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteToolbar;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateUnspecifiedTypeRequest;
import org.eclipse.gmf.runtime.diagram.ui.tools.UnspecifiedTypeConnectionTool;
import org.eclipse.gmf.runtime.diagram.ui.tools.UnspecifiedTypeCreationTool;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;

import soundgates.diagram.providers.SoundgatesElementTypes;
import soundgates.diagram.soundcomponents.AtomicSoundComponentLibrary;
import soundgates.diagram.soundcomponents.CompositeSoundComponentLibrary;

/**
 * @generated
 */
public class SoundgatesPaletteFactory {

	/**
	 * @generated NOT
	 */
	public void fillPalette(PaletteRoot paletteRoot) {
		PaletteToolbar standardGroup = ((PaletteToolbar) paletteRoot
				.getChildren().get(0));
		paletteRoot.add(createComponents1Group());
		paletteRoot.add(createComponents2Group());
		paletteRoot.add(createPorts2Group());
		paletteRoot.add(createConnections3Group());
	}

	/**
	 * Creates "Atomic Components" palette tool group
	 * 
	 * @generated NOT
	 */
	private PaletteContainer createComponents1Group() {
		PaletteDrawer paletteContainer = new PaletteDrawer(
				"Atomic sound components");
		paletteContainer.setId("createComponents1Group"); //$NON-NLS-1$
		// We don't want to create untyped AtomicSoundComponents
		// paletteContainer.add(createAtomicSoundComponent1CreationTool());
		List<String> availableTypes = AtomicSoundComponentLibrary.getInstance()
				.getAvailableTypes();
		for (int i = 0; i < availableTypes.size(); i++) {
			paletteContainer
					.add(createConcreteAtomicNodeCreationTool(availableTypes
							.get(i))); // MYTOOL
		}
		return paletteContainer;
	}

	/**
	 * Creates "Composite Components" palette tool group
	 * 
	 * @generated NOT
	 */
	private PaletteContainer createComponents2Group() {
		PaletteDrawer paletteContainer = new PaletteDrawer(
				"Composite sound components");
		paletteContainer.setId("createComponents2Group"); //$NON-NLS-1$

		paletteContainer.add(createCompositeSoundComponent2CreationTool());

		List<String> availableComponents = CompositeSoundComponentLibrary
				.getInstance().getAvailableComponents();
		for (int i = 0; i < availableComponents.size(); i++) {
			paletteContainer
					.add(createConcreteCompositeNodeCreationTool(availableComponents
							.get(i))); // MYTOOL
		}

		return paletteContainer;
	}

	/**
	 * Creates "Ports" palette tool group
	 * 
	 * @generated NOT
	 */
	private PaletteContainer createPorts2Group() {
		PaletteDrawer paletteContainer = new PaletteDrawer("Ports");
		paletteContainer.setId("createPorts2Group"); //$NON-NLS-1$
		paletteContainer.add(createPort1CreationTool());
		return paletteContainer;
	}

	/**
	 * Creates "Connections" palette tool group
	 * 
	 * @generated NOT
	 */
	private PaletteContainer createConnections3Group() {
		PaletteDrawer paletteContainer = new PaletteDrawer("Connections");
		paletteContainer.setId("createConnections3Group"); //$NON-NLS-1$
		paletteContainer.setDescription(Messages.Connections3Group_desc);
		paletteContainer.add(createLink1CreationTool());
		paletteContainer.add(createDelegation2CreationTool());
		return paletteContainer;
	}

	/**
	 * @generated NOT
	 */
	private ToolEntry createCompositeSoundComponent2CreationTool() {
		ArrayList<IElementType> types = new ArrayList<IElementType>(2);
		types.add(SoundgatesElementTypes.CompositeSoundComponent_2002);
		types.add(SoundgatesElementTypes.CompositeSoundComponent_3003);
		NodeToolEntry entry = new NodeToolEntry(
				"New composite sound component",
				Messages.CompositeSoundComponent2CreationTool_desc, types);
		entry.setId("createCompositeSoundComponent2CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(SoundgatesElementTypes
				.getImageDescriptor(SoundgatesElementTypes.CompositeSoundComponent_2002));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createPort1CreationTool() {
		NodeToolEntry entry = new NodeToolEntry(
				Messages.Port1CreationTool_title,
				Messages.Port1CreationTool_desc,
				Collections.singletonList(SoundgatesElementTypes.Port_3001));
		entry.setId("createPort1CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(SoundgatesDiagramEditorPlugin
				.findImageDescriptor("/Soundgates.edit/icons/toolspalette/PortIn.gif")); //$NON-NLS-1$
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createLink1CreationTool() {
		ArrayList<IElementType> types = new ArrayList<IElementType>(2);
		types.add(SoundgatesElementTypes.Link_4001);
		types.add(SoundgatesElementTypes.Link_4002);
		LinkToolEntry entry = new LinkToolEntry(
				Messages.Link1CreationTool_title,
				Messages.Link1CreationTool_desc, types);
		entry.setId("createLink1CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(SoundgatesElementTypes
				.getImageDescriptor(SoundgatesElementTypes.Link_4001));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createDelegation2CreationTool() {
		LinkToolEntry entry = new LinkToolEntry(
				Messages.Delegation2CreationTool_title,
				Messages.Delegation2CreationTool_desc,
				Collections
						.singletonList(SoundgatesElementTypes.Delegation_4003));
		entry.setId("createDelegation2CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(SoundgatesElementTypes
				.getImageDescriptor(SoundgatesElementTypes.Delegation_4003));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createAtomicSoundComponent1CreationTool() {
		ArrayList<IElementType> types = new ArrayList<IElementType>(2);
		types.add(SoundgatesElementTypes.AtomicSoundComponent_2001);
		types.add(SoundgatesElementTypes.AtomicSoundComponent_3002);
		NodeToolEntry entry = new NodeToolEntry(
				Messages.AtomicSoundComponent1CreationTool_title,
				Messages.AtomicSoundComponent1CreationTool_desc, types);
		entry.setId("createAtomicSoundComponent1CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(SoundgatesElementTypes
				.getImageDescriptor(SoundgatesElementTypes.AtomicSoundComponent_2001));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	// MYTOOL

	// atomic components
	private ToolEntry createConcreteAtomicNodeCreationTool(String atomicType) {

		ArrayList<IElementType> types = new ArrayList<IElementType>(2);
		types.add(SoundgatesElementTypes.AtomicSoundComponent_2001);
		types.add(SoundgatesElementTypes.AtomicSoundComponent_3002);

		NodeToolEntry entry = new ConcreteAtomicNodeToolEntry(atomicType,
				"Create " + atomicType, types);
		entry.setId("createAtomicSoundComponent1CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(SoundgatesElementTypes
				.getImageDescriptor(SoundgatesElementTypes.AtomicSoundComponent_2001));
		entry.setLargeIcon(entry.getSmallIcon()); // TODO Icon anpassen?
		return entry;
	}

	private static class ConcreteAtomicTypeCreationTool extends
			UnspecifiedTypeCreationTool {
		private String atomicType;

		public ConcreteAtomicTypeCreationTool(List elementTypes,
				String atomicType) {
			super(elementTypes);
			this.atomicType = atomicType;
		}

		protected Request createTargetRequest() {
			Request r = super.createTargetRequest();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("concreteType", this.atomicType);
			r.setExtendedData(map);
			return r;
		}
	}

	private static class ConcreteAtomicNodeToolEntry extends NodeToolEntry {
		private String atomicType;

		protected ConcreteAtomicNodeToolEntry(String title, String description,
				List<IElementType> elementTypes) {
			super(title, description, elementTypes);
			this.atomicType = title;
		}

		public Tool createTool() {
			Tool tool = new ConcreteAtomicTypeCreationTool(elementTypes,
					atomicType);
			tool.setProperties(getToolProperties());
			return tool;
		}
	}

	// composite components
	private ToolEntry createConcreteCompositeNodeCreationTool(String name) {

		ArrayList<IElementType> types = new ArrayList<IElementType>(2);
		types.add(SoundgatesElementTypes.CompositeSoundComponent_2002);
		types.add(SoundgatesElementTypes.CompositeSoundComponent_3003);

		NodeToolEntry entry = new ConcreteCompositeNodeToolEntry(name,
				"Create " + name, types);
		entry.setId("createCompositeSoundComponent1CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(SoundgatesElementTypes
				.getImageDescriptor(SoundgatesElementTypes.CompositeSoundComponent_2002));
		entry.setLargeIcon(entry.getSmallIcon()); // TODO Icon anpassen?
		return entry;
	}

	private static class ConcreteCompositeCreationTool extends
			UnspecifiedTypeCreationTool {
		private String name;

		public ConcreteCompositeCreationTool(List names, String name) {
			super(names);
			this.name = name;
		}

		protected Request createTargetRequest() {
			Request r = super.createTargetRequest();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", this.name);
			r.setExtendedData(map);
			return r;
		}
	}

	private static class ConcreteCompositeNodeToolEntry extends NodeToolEntry {
		private String name;

		protected ConcreteCompositeNodeToolEntry(String title,
				String description, List<IElementType> names) {
			super(title, description, names);
			this.name = title;
		}

		public Tool createTool() {
			Tool tool = new ConcreteCompositeCreationTool(elementTypes, name);
			tool.setProperties(getToolProperties());
			return tool;
		}
	}

	// ---MYTOOL

	/**
	 * @generated
	 */
	private static class NodeToolEntry extends ToolEntry {

		/**
		 * @generated NOT
		 */
		protected final List<IElementType> elementTypes;

		/**
		 * @generated
		 */
		private NodeToolEntry(String title, String description,
				List<IElementType> elementTypes) {
			super(title, description, null, null);
			this.elementTypes = elementTypes;
		}

		/**
		 * @generated
		 */
		public Tool createTool() {
			Tool tool = new UnspecifiedTypeCreationTool(elementTypes);
			tool.setProperties(getToolProperties());
			return tool;
		}
	}

	/**
	 * @generated
	 */
	private static class LinkToolEntry extends ToolEntry {

		/**
		 * @generated
		 */
		private final List<IElementType> relationshipTypes;

		/**
		 * @generated
		 */
		private LinkToolEntry(String title, String description,
				List<IElementType> relationshipTypes) {
			super(title, description, null, null);
			this.relationshipTypes = relationshipTypes;
		}

		/**
		 * @generated
		 */
		public Tool createTool() {
			Tool tool = new UnspecifiedTypeConnectionTool(relationshipTypes);
			tool.setProperties(getToolProperties());
			return tool;
		}
	}
}
