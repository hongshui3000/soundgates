/*
 * TemplateSoundComponent_SW.cpp
 *
 *  Created on: Jan 23, 2014
 *      Author: gwue
 */

#include "FIR_HW.hpp"

#ifndef ZYNQ

/* do nothing */

#else

FIR_HW::FIR_HW(std::vector<std::string> params) : FIR(params), m_HWTSlot(FIR::name) {

}

void FIR_HW::init(){

    // You can init() sound output ports to clear their buffers
    m_SoundOut_1_Port->init();

    // Control values can trigger a callback function when they change.
    m_CutOffFrequency_2_Port->registerCallback(ICallbackPtr(new OnCutoffChangeHW(*this)));

    if(m_HWTSlot.isValid()){
        /* 1. initialize mailboxes */
        mbox_init(&m_CtrlStart, 1);
        mbox_init(&m_CtrlStop,  1);

        /* 2. initialize reconos */
        reconos_init();

        m_ReconOSResource[0].type = RECONOS_TYPE_MBOX;
        m_ReconOSResource[0].ptr  = &m_CtrlStart;

        m_ReconOSResource[1].type = RECONOS_TYPE_MBOX;
        m_ReconOSResource[1].ptr  = &m_CtrlStop;


        m_HWTParams.args[0] = (uint32_t) m_SoundIn_1_Port->getReadBuffer();
        m_HWTParams.args[1] = (uint32_t) m_SoundIn_1_Port->getWriteBuffer();

        reconos_hwt_setresources(&m_ReconOSThread, &m_ReconOSResource[0], 2);
        reconos_hwt_setinitdata(&m_ReconOSThread, (void *) &m_HWTParams.args[0]);

        reconos_hwt_create(&m_ReconOSThread, m_HWTSlot.getSlot(), NULL);

    }
}

void FIR_HW::process(){

    m_HWTParams.args[0] = (uint32_t) m_SoundIn_1_Port->getReadBuffer();
    m_HWTParams.args[1] = (uint32_t) m_SoundOut_1_Port->getWriteBuffer();

    mbox_put(&m_CtrlStart, FIR_HWT_START);
    mbox_get(&m_CtrlStop);
}

#endif
