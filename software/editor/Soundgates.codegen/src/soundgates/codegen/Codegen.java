package soundgates.codegen;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import soundgates.AtomicSoundComponent;
import soundgates.CompositeSoundComponent;
import soundgates.DataType;
import soundgates.Delegation;
import soundgates.Direction;
import soundgates.Element;
import soundgates.Link;
import soundgates.Patch;
import soundgates.Port;
import soundgates.SoundComponent;
import soundgates.SoundgatesPackage;
import soundgates.diagram.soundcomponents.AtomicSoundComponentXMLHandler;

public class Codegen {

	private final String pdcodeFolderName = "pdcode";
	HashMap<Port, Integer> compositeComponentPortMappings;

	IFolder pdCodeFolder = null;
	
	public Codegen() {
	}
	
	public Patch getPatch(String path){
	    SoundgatesPackage.eINSTANCE.eClass();
	    
	    // Register the XMI resource factory for the .xmi extension

	    Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
	    Map<String, Object> m = reg.getExtensionToFactoryMap();
	    m.put("xmi", new XMIResourceFactoryImpl());

	    // Obtain a new resource set
	    ResourceSet resSet = new ResourceSetImpl();

	    // Get the resource
	    Resource resource = resSet.getResource(URI.createURI(path), true);
	    // Get the first model element and cast it to the right type, in my
	    // example everything is hierarchical included in this first node
	    Patch patch = (Patch) resource.getContents().get(0);
	    return patch;		
	}
	
	public void generateCodeForPatch(Patch patch) throws IOException, CoreException {
		compositeComponentPortMappings = new HashMap<Port, Integer>();
		List<SoundComponent> componentList = new ArrayList<SoundComponent>();
		List<Link> linkList = new ArrayList<Link>();		
		
		buildCompositeMappings(patch);
		
		for(Element element : patch.getElements()){
			if(element instanceof SoundComponent){				 
				componentList.add((SoundComponent) element);

				if(element instanceof CompositeSoundComponent){					
					handleCompositeSoundComponent( (CompositeSoundComponent) element);
				} else if(element instanceof AtomicSoundComponent)
				{					
					printAtomicComponent((AtomicSoundComponent) element);
				}		
				
			}			
			else if(element instanceof Link){
				linkList.add((Link) element);
			}
		}		
		printPatch(componentList, linkList);
	}
	
	private void buildCompositeMappings(Patch patch) {
		
		for(Element element : patch.getElements()){
			if (element instanceof CompositeSoundComponent){
				buildCompositeMappings((CompositeSoundComponent)element);
			}
		}
	}

	private void buildCompositeMappings(CompositeSoundComponent element) {
		int inlets = 0;
		int outlets = 0;
		
		for (SoundComponent component : element.getEmbeddedComponents()){
			if (component instanceof CompositeSoundComponent){
				buildCompositeMappings((CompositeSoundComponent) component);
			}
		}
		
		for (Delegation delegation : element.getDelegations()){
			Port sourcePort = delegation.getSource();
			Port targetPort = delegation.getTarget();
			
			if (sourcePort.getDirection().equals(Direction.IN)){
				compositeComponentPortMappings.put(sourcePort, inlets++);
			}
			
			if (targetPort.getDirection().equals(Direction.OUT)){
				compositeComponentPortMappings.put(targetPort, outlets++);
			}
		}
	}

	private HashMap<SoundComponent, Integer> componentId = new HashMap<SoundComponent, Integer>();
	private Integer lastUsedId = 0;
	
	private String getUniqueName(SoundComponent soundComponent){
		if (!componentId.containsKey(soundComponent)){
			componentId.put(soundComponent, lastUsedId++);
		}
		return "component_" + componentId.get(soundComponent) + "_" + soundComponent.getName();
	}
	
	private void printAtomicComponent(AtomicSoundComponent atomicSoundComponent)  throws CoreException {
		IFile file = pdCodeFolder.getFile(getUniqueName(atomicSoundComponent) + ".pd");
		if (atomicSoundComponent.getStringProperties() != null){
			
			String pdCodeText = "";
			
			// get pd code
			if (atomicSoundComponent.getStringProperties().containsKey(AtomicSoundComponentXMLHandler.CODEGEN_PREFIX_PDCODE)){
				pdCodeText = atomicSoundComponent.getStringProperties().get(AtomicSoundComponentXMLHandler.CODEGEN_PREFIX_PDCODE);				
			}
			
			// get properties mapping
			if (atomicSoundComponent.getStringProperties().
					containsKey(AtomicSoundComponentXMLHandler.CODEGEN_PREFIX_PROP_MAPPINGS)){
			
				//example: PropName="Value"|Tag="@value"||...
				String propMapping = atomicSoundComponent.getStringProperties().get(AtomicSoundComponentXMLHandler.CODEGEN_PREFIX_PROP_MAPPINGS);
				
				// parse properties
				String[] entries = propMapping.split("\\|\\|");
				
				for (String entry : entries) {
					if (entry.contains("|")) {
						String[] subEntry = entry.split("\\|");
						String[] propNameEntry = subEntry[0].split("=");
						String propName = propNameEntry[1].replace("\"", "");
						String[] tagEntry = subEntry[1].split("=");
						String tag = tagEntry[1].replace("\"", "");

						// search for the property
						String propValue;
						try {
							propValue = propertySearch(atomicSoundComponent,
									propName);
							// replace all tags with the value of the property
							pdCodeText = pdCodeText.replaceAll(" " + tag + " ",
									" " + propValue + " ");
							pdCodeText = pdCodeText.replaceAll(" " + tag + ";",
									" " + propValue + ";");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}				
			}

			InputStream code = new ByteArrayInputStream(pdCodeText.getBytes());
			file.create(code, IResource.FORCE, null);
		}
	}
	
	public void handleCompositeSoundComponent(CompositeSoundComponent compositeSoundComponent) throws IOException, CoreException {

		// create lists
		List<SoundComponent> componentList = new ArrayList<SoundComponent>();
		List<Link> linkList = new ArrayList<Link>();
		List<Delegation> delegationList = new ArrayList<Delegation>();
		
		// add and handle embedded components
		for(SoundComponent soundComponent : compositeSoundComponent.getEmbeddedComponents()){
			componentList.add(soundComponent);
			
			if(soundComponent instanceof AtomicSoundComponent)
			{				
				printAtomicComponent((AtomicSoundComponent) soundComponent);
			}
			else if(soundComponent instanceof CompositeSoundComponent){
				handleCompositeSoundComponent((CompositeSoundComponent) soundComponent);
			}			
		}
		
		// add embedded links
		for(Link link : compositeSoundComponent.getLinks())
			linkList.add(link);
		for(Delegation delegation : compositeSoundComponent.getDelegations())
			delegationList.add(delegation);
		
		printCompositeSoundComponent(compositeSoundComponent, componentList, linkList, delegationList);
	}
	
	private HashMap<Port, Integer> parsePortMappings(AtomicSoundComponent atomicSoundComponent){
		HashMap<Port,Integer> resultingMapping = new HashMap<Port, Integer>();
		// get port mapping
		if (atomicSoundComponent.getStringProperties().
				containsKey(AtomicSoundComponentXMLHandler.CODEGEN_PREFIX_PORT_MAPPINGS)){
		
			//example: PortName="Value"|PortNumber="0"||...
			String portMapping = atomicSoundComponent.getStringProperties().get(AtomicSoundComponentXMLHandler.CODEGEN_PREFIX_PORT_MAPPINGS);
			
			// parse ports
			String[] entries = portMapping.split("\\|\\|");
			
			for (String entry : entries) {
				if (entry.contains("|")) {
					String[] subEntry = entry.split("\\|");
					String[] portNameEntry = subEntry[0].split("=");
					String portName = portNameEntry[1].replace("\"", "");
					String[] portNumberEntry = subEntry[1].split("=");
					Integer portNumber = Integer.parseInt(portNumberEntry[1].replace("\"", ""));

					// search for the port
					try {
						Port port = portSearch(atomicSoundComponent, portName);
						resultingMapping.put(port, portNumber);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return resultingMapping;
	}
	
	private void printPatch(List<SoundComponent> componentList, List<Link> linkList) throws CoreException{
		IFile file = pdCodeFolder.getFile("patch.pd");
		String result = "#N canvas 621 551 450 300 10; \n";
		for (SoundComponent comp : componentList){
				result += "#X obj 0 0 "+ getUniqueName(comp) + ";\n";
		}		
		result += handleLinks(linkList, componentList);
		file.create(new ByteArrayInputStream(result.getBytes()), IFile.FORCE, null);
	}
	
	private String handleLinks(List<Link> linkList, List<SoundComponent> componentList){
		StringBuilder pdcode = new StringBuilder();
		for (Link link : linkList){
			SoundComponent sourceComponent = link.getSource().getComponent();
			SoundComponent targetComponent = link.getTarget().getComponent();
			int source = componentList.indexOf(sourceComponent);
			int sink = componentList.indexOf(targetComponent);
			int sinkPort = 0;
			int sourcePort = 0;
			
			if (sourceComponent instanceof AtomicSoundComponent) {
				HashMap<Port, Integer> sourcePortMapping = parsePortMappings((AtomicSoundComponent) sourceComponent);
				sourcePort = sourcePortMapping.get(link.getSource());
			}
			if (targetComponent instanceof AtomicSoundComponent) {
				HashMap<Port, Integer> targetPortMapping = parsePortMappings((AtomicSoundComponent) targetComponent);
				sinkPort = targetPortMapping.get(link.getTarget());
			}
			if (sourceComponent instanceof CompositeSoundComponent){
				sourcePort = compositeComponentPortMappings.get(link.getSource());
			}
			if (targetComponent instanceof CompositeSoundComponent){
				sinkPort = compositeComponentPortMappings.get(link.getTarget());
			}
			
			pdcode.append("#X connect " + source + " " + sourcePort + " " + sink + " " + sinkPort + ";\n");
		}
		return pdcode.toString();
	}
	
	
	private void printCompositeSoundComponent(CompositeSoundComponent compositeComponent, List<SoundComponent> componentList, List<Link> linkList, List<Delegation> delegationList) throws CoreException{
		IFile file = pdCodeFolder.getFile(getUniqueName(compositeComponent) +".pd");
		String result = "#N canvas 621 551 450 300 10; \n";		
		
		for (SoundComponent comp : componentList){
				result += "#X obj " + componentList.indexOf(comp) + " 0 "+ getUniqueName(comp) + ";\n";
		}
		
		result += handleLinks(linkList, componentList) + "\n";
		result += handleDelegations(delegationList, componentList);
		
		file.create(new ByteArrayInputStream(result.getBytes()), IResource.FORCE, null);
		
	}		
	
	private String handleDelegations(List<Delegation> delegationList,
			List<SoundComponent> componentList) {
		String result = "";
		
		int createdObjects = 0;
		
		for (Delegation delegation : delegationList){
			Port sourcePort = delegation.getSource();
			Port targetPort = delegation.getTarget();
			
			if (sourcePort.getDirection().equals(Direction.IN)){
				// use the port mapping as x coordinate to get puredate to use that number for the inlet
				result += "#X obj " + compositeComponentPortMappings.get(sourcePort) + " 0 inlet";
				createdObjects++;
				if (sourcePort.getDataType().equals(DataType.SOUND)){
					result += "~";
				}
				result += ";\n";
				
				SoundComponent targetComponent = targetPort.getComponent();
				result += "#X connect " + (componentList.size() + createdObjects - 1) + " 0 ";
				result += componentList.indexOf(targetPort.getComponent()) + " ";
				if (targetComponent instanceof AtomicSoundComponent){
					result += parsePortMappings((AtomicSoundComponent) targetComponent).get(targetPort);
				} else if (targetComponent instanceof CompositeSoundComponent){
					result += compositeComponentPortMappings.get(targetPort);
				}
				result += ";\n";
			}
			
			if (targetPort.getDirection().equals(Direction.OUT)){
				result += "#X obj " + createdObjects + " 0 outlet";
				createdObjects++;
				if (sourcePort.getDataType().equals(DataType.SOUND)){
					result += "~";
				}
				result += ";\n";
				
				SoundComponent sourceComponent = sourcePort.getComponent();
				result += "#X connect " + componentList.indexOf(sourcePort.getComponent()) + " ";
				if (sourceComponent instanceof AtomicSoundComponent){
					result += parsePortMappings((AtomicSoundComponent) sourceComponent).get(sourcePort);
				} else if (sourceComponent instanceof CompositeSoundComponent){
					result += compositeComponentPortMappings.get(sourcePort);
				}
				result += " " + (componentList.size() + createdObjects - 1) + " 0 ";

				result += ";\n";
			}
		}
		return result;
	}
	
	private Port portSearch(AtomicSoundComponent atomicSoundComponent, String portName) throws Exception{
		for (Port port : atomicSoundComponent.getPorts()){
			if (port.getName().equals(portName)){
				return port;
			}
		}
		throw new Exception("Port not found in component");
	}
	
	private String propertySearch(AtomicSoundComponent atomicSoundComponent, String propName) throws Exception{
		if (atomicSoundComponent.getBooleanProperties().containsKey(propName))
			return atomicSoundComponent.getBooleanProperties().get(propName).toString();
		if (atomicSoundComponent.getIntegerProperties().containsKey(propName))
			return atomicSoundComponent.getIntegerProperties().get(propName).toString();
		if (atomicSoundComponent.getFloatProperties().containsKey(propName))
			return atomicSoundComponent.getFloatProperties().get(propName).toString();	
		
		throw new Exception("Property name not in the list!");
	}

	public void generate(IFile file) throws CoreException, IOException {
		Patch patch = getPatch(file.getFullPath().toPortableString());

		IProject project = file.getProject();
		pdCodeFolder = project.getFolder(pdcodeFolderName);
		if (pdCodeFolder.exists())
			pdCodeFolder.delete(true, null);
			pdCodeFolder.create(IResource.NONE, true, null);
		generateCodeForPatch(patch);
	}
}