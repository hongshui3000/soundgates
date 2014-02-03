/*
 * TemplateComponent.hpp
 *
 *  Created on: Jan 23, 2014
 *      Author: gwue
 */

#ifndef FIR_HPP_
#define FIR_HPP_

#include <sstream>
#include <climits>

#include <Synthesizer.h>
#include <SoundComponentImpl.h>
#include <SoundPort.h>
#include <ControlPort.h>


#include "fir_coeff_lp_.hpp"
#include "fir_coeff_hp_.hpp"

#define N_FIR_COEFF          29      // Number of filter coefficients
#define N_CUTOFF_VECTOR_SIZE 8001

#define FIR_TYPE_LP "lp"
#define FIR_TYPE_HP "hp"
#define FIR_TYPE_BP "bp"

class FIR: public SoundComponentImpl
{

public:

    int32_t**   m_coeff;

    int32_t*    m_currCoeff;

	float       m_CutOffFrequency;

	DECLARE_COMPONENTNAME

	DECLARE_PORT3(ControlPort, CutOffFrequency, 1);
	DECLARE_PORT3(SoundPort, SoundIn, 2);
	DECLARE_PORT3(SoundPort, SoundOut, 1);

	FIR(std::vector<std::string> params);
	virtual ~FIR();

	virtual void init(void);

	virtual void process(void) = 0;

};

class OnCutoffChange : public ICallbackFunctor {
private:
    FIR& m_ObjRef;
public:
    OnCutoffChange(FIR& ref ) : m_ObjRef(ref){ }

    void operator()(){
    	// The first new control value can be accessed like this.
    	float val = m_ObjRef.m_CutOffFrequency_1_Port->pop();

        m_ObjRef.m_CutOffFrequency = val;

        m_ObjRef.m_currCoeff    = m_ObjRef.m_coeff[(int) val];
    }
};

#endif /* TEMPLATECOMPONENT_HPP_ */
