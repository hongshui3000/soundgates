/*
 * SineImpl_HW.h
 *
 *  Created on: Nov 29, 2013
 *      Author: lukas
 */

#ifndef SINEIMPL_HW_H_
#define SINEIMPL_HW_H_


#include "../SineSoundComponent.h"

#include <HWSlot.h>
#include <HWTParameters.h>
#include <stdint.h>

#ifdef ZYNQ

extern "C"{
    #include <reconos/reconos.h>
    #include <reconos/mbox.h>
}

#define SINUS_HWT_START 0x0F
#define SINUS_HWT_STOP  0xF0

class SineSoundComponent_HW : public SineSoundComponent{
private:

    HWSlot slot;

    /* Parameter struct: 1 src address, 1 argument */
    uint32_t m_HWTParams[3];

    struct mbox m_CtrlStart;
    struct mbox m_CtrlStop;
    char*  m_LocalBuffer;
    /* Two message box resources */
    struct reconos_resource m_ReconOSResource[2];
    struct reconos_hwt      m_ReconOSThread;
public:

    SineSoundComponent_HW(std::vector<std::string>);
    virtual ~SineSoundComponent_HW();
    void init();
    void process();

};

#else


class SineSoundComponent_HW : public SineSoundComponent {

public:

    SineSoundComponent_HW(std::vector<std::string>);
    virtual ~SineSoundComponent_HW();
    void init();
    void process();
};

#endif

#endif /* SINEIMPL_HW_H_ */
