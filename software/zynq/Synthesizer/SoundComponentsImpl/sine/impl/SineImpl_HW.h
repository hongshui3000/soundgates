/*
 * SineImpl_HW.h
 *
 *  Created on: Nov 29, 2013
 *      Author: lukas
 */

#ifndef SINEIMPL_HW_H_
#define SINEIMPL_HW_H_

#include "../SineSoundComponent.h"

class SineImpl_HW : public SineSoundComponent {

public:

	SineImpl_HW(std::vector<std::string>);

	void init();
	void process();

};

#endif /* SINEIMPL_HW_H_ */
