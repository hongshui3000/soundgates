/*
 * TriangleSoundComponent_HW.cpp
 *
 *  Created on: Nov 29, 2013
 *      Author: CaiusC
 */


#include "TriangleSoundComponent_HW.h"

#ifndef ZYNQ

TriangleSoundComponent_HW::TriangleSoundComponent_HW(std::vector<std::string> params) : TriangleSoundComponent(params){
}

TriangleSoundComponent_HW::~TriangleSoundComponent_HW(){}



void TriangleSoundComponent_HW::init(){ }

void TriangleSoundComponent_HW::process(){ }

#else

TriangleSoundComponent_HW::TriangleSoundComponent_HW(std::vector<std::string> params)
    : TriangleSoundComponent(params),
    slot(TriangleSoundComponent::name) {

    m_LocalBuffer = new char[Synthesizer::config::bytesPerBlock];


}

TriangleSoundComponent_HW::~TriangleSoundComponent_HW(){
    delete m_LocalBuffer;
}

void TriangleSoundComponent_HW::init(){

    m_FrequencyIn_1_Port->registerCallback(ICallbackPtr(new OnFrequencyChange_HW(*this)));


    /* initialize reconos */

    if(slot.isValid()){
        // TODO: Warum werden hier zwei message boxen benötigt!?

        /* initialize message boxes with 1 data word */
        mbox_init(&m_CtrlStart, 1);
        mbox_init(&m_CtrlStop,  1);

        m_HWTParams[0] = (uint32_t) m_LocalBuffer;

        m_HWTParams[1]  = 0;
        m_HWTParams[2]  = (uint32_t) (getPhaseIncrement_HW(440));

        m_ReconOSResource[0].type = RECONOS_TYPE_MBOX;
        m_ReconOSResource[0].ptr  = &m_CtrlStart;

        m_ReconOSResource[1].type = RECONOS_TYPE_MBOX;
        m_ReconOSResource[1].ptr  = &m_CtrlStop;

        reconos_hwt_setresources(&m_ReconOSThread, &m_ReconOSResource[0], 2);
        reconos_hwt_setinitdata(&m_ReconOSThread, (void *) &m_HWTParams[0]);

        reconos_hwt_create(&m_ReconOSThread, slot.getSlot(), NULL);

        m_SoundOut_1_Port->clearWriteBuffer();
    }
}

void TriangleSoundComponent_HW::process(){
    if (this->m_active) {
        m_HWTParams[2] = (uint32_t) (m_PhaseIncr); //(uint32_t) (m_PhaseIncr *  SOUNDGATES_FIXED_PT_SCALE);

		mbox_put(&m_CtrlStart, SINUS_HWT_START);
		mbox_get(&m_CtrlStop);                   /* Blocks until thread ready */

		memcpy(m_SoundOut_1_Port->getWriteBuffer(), &m_LocalBuffer[0], Synthesizer::config::bytesPerBlock);
    }
    else {
    	m_SoundOut_1_Port->clearWriteBuffer();
   	}
}

#endif