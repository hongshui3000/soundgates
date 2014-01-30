/*
 * TemplateComponent.cpp
 *
 *  Created on: Jan 23, 2014
 *      Author: gwue
 */

#include "FIR.hpp"

// include the concrete implementation headers.
#include "impl/FIR_SW.hpp"
// #include "impl/TemplateSoundComponent_HW.hpp"

// Macro to define the Component name. This will be the name that will later appear in the TGF files
DEFINE_COMPONENTNAME(FIR, "fir")

// Export the sound compononent such that it can be compiled as a shared object
// Different possibilities here:
// EXPORT_SOUNDCOMPONENT_MIXED_IMPL(CLASSNAME) --> both HW and SW implementations exist
// EXPORT_SOUNDCOMPONENT_SW_ONLY(CLASSNAME)
// EXPORT_SOUNDCOMPONENT_HW_ONLY(CLASSNAME)
// EXPORT_SOUNDCOMPONENT_NO_IMPL(CLASSNAME) --> no impl/ subclass, processing is done right here.
//												can be used for very simple stuff like constants.
//												preferably use SW_ONLY instead

EXPORT_SOUNDCOMPONENT_SW_ONLY(FIR);

FIR::FIR(std::vector<std::string> params) :
		SoundComponentImpl(params)
{
	// Register ports (that you have declared in the header file)
	// CREATE_AND_REGISTER_PORT3(<ComponentClass>, In|Out, SoundPort|ControlPort, <Name>, <Portumber>
	// <ComponentClass> is the name of this component
	// Either In or Out to specify the direction of this port
	// Type of the Port
	// <Name> as defined in the header
	// <Portnumber> as defined in the header

	// For: DECLARE_PORT3(ControlPort, TemplateControlIn, 1);
	CREATE_AND_REGISTER_PORT3(FIR, In, ControlPort, CutOffFrequency, 1);

	// For: DECLARE_PORT3(SoundPort, TemplateSoundIn, 2);
	CREATE_AND_REGISTER_PORT3(FIR, In, SoundPort, SoundIn, 2);

	// For: DECLARE_PORT3(SoundPort, TemplateSoundOut, 2);
	CREATE_AND_REGISTER_PORT3(FIR, Out, SoundPort, SoundOut, 1);
}

FIR::~FIR(){}

void FIR::init()
{
	// You can init() sound output ports to clear their buffers
	m_SoundOut_1_Port->init();

	// Control values can trigger a callback function when they change.
	m_CutOffFrequency_1_Port->registerCallback(ICallbackPtr(new OnChange(*this)));
}

class OnChange : public ICallbackFunctor {
private:
    FIR& m_ObjRef;
public:
    OnChange(FIR& ref ) : m_ObjRef(ref){ }

    void operator()(){
    	// The first new control value can be accessed like this.
    	float val = m_ObjRef.m_CutOffFrequency_1_Port->pop();
        m_ObjRef.cutOffFrequency = val;
    }
};

// ::process is not implemented here but in the SW subclass!
