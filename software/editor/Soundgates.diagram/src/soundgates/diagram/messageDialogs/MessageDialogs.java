package soundgates.diagram.messageDialogs;

import org.eclipse.jface.dialogs.MessageDialog;

public class MessageDialogs {

	public static boolean replaceExistingCompositeSoundComponentDialog(String name){	
	
	   MessageDialog dialog = new MessageDialog(
			      null, "Composite sound component "+name+" already exists", 
			      null, "Do you want to replace the file "+name+".xml?",
			      MessageDialog.QUESTION,
			      new String[] {"Yes", "No"},
			      0); 
	   
	   int result =  dialog.open();
	   if(result==0) return true;
	   else return false;
	}
	
	// --INFO--
	
	public static void compositeSoundComponentWasExported(String name){		
		MessageDialog.openInformation(null, "Success", "The composite sound component \""+name+"\" was exported to file \""+name+".xml\"");
	}
	
	
	// --WARNINGS--

	public static void compositeSoundComponentHasNoName(){
		MessageDialog.openWarning(null, "Composite sound component has no name", "Please enter a name for the composite sound component.");
	}
	
	public static void notAllPortsHaveAName(String parentCompName){
		MessageDialog.openWarning(null, "Not all ports of \""+parentCompName+"\" have a name", "Please enter a name for each port of the composite sound component \""+parentCompName+"\""); 
	}

	public static void portHasNoOutgoingConnection(String parent, String name) {		
		MessageDialog.openWarning(null, "Port \""+name+"\" of \""+parent+"\" has no outgoing link", "Please create an outgoing connection for the port \""+name+"\"");
	}

	public static void portHasNoIncomingConnection(String parent, String name) {
		MessageDialog.openWarning(null, "Port \""+name+"\" of \""+parent+"\"  has no incoming link", "Please create an incoming connection for the port \""+name+"\"");		
	}
		
	public static void inPortHasOutgoingConnection(String parent, String name) {
		MessageDialog.openWarning(null, "IN-port \""+name+"\" of \""+parent+"\"  has an outgoing link", "Please delete the outgoing connection from the port \""+name+"\"");		
	}
	
	public static void outPortHasIncomingConnection(String parent, String name) {
		MessageDialog.openWarning(null, "OUT-port \""+name+"\" of \""+parent+"\"  has an incoming link", "Please delete the incoming connection from the port "+name+"\"");		
	}
	
	public static void inPortAsSource(String parent, String port) {		
		MessageDialog.openWarning(null, "Link error", "A link in \""+parent+"\" has the IN-port \""+port+"\" as source.");		
	}
	
	public static void outPortAsTarget(String parent, String port) {		
		MessageDialog.openWarning(null, "Link error", "A link in \""+parent+"\" has the OUT-port \""+port+"\" as target.");		
	}
	
	public static void delegationError(String parent, String source, String target) {		
		MessageDialog.openWarning(null, "A delegation must connect two IN-ports or two OUT-ports", "The delegation in \""+parent+"\" connects the port \""+source+"\" with the port \""+target+"\"");		
	}
	
	public static void delegation2Error(String parent, String source, String target) {		
		MessageDialog.openWarning(null, "", "The delegation in \""+parent+"\" between the port \""+source+"\" and the port \""+target+"\" is incorrect" );		
	}

	public static void compositeSoundComponentHasNoInPort(String name) {		
		MessageDialog.openWarning(null, "Composite sound component \""+name+"\" has no IN-ports", "Please add at least one IN-port to the composite sound component \""+name+"\"");		
	}
	
	public static void compositeSoundComponentHasNoOutPort(String name) {		
		MessageDialog.openWarning(null, "Composite sound component \""+name+"\" has no OUT-ports", "Please add at least one OUT-port to the composite sound component \""+name+"\"");			
	}
	
	public static void compositeSoundComponentHasNoEmbeddedComponents(String name) {		
		MessageDialog.openWarning(null, "Composite sound component \""+name+"\" has no embedded components", "Please add at least one embedded component to the composite sound component \""+name+"\"");		
	}
}
