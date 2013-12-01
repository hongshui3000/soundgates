/*
 * SineImpl_SW.cpp
 *
 *  Created on: Nov 29, 2013
 *      Author: lukas
 */

#include "SineImpl_SW.h"



SineImpl_SW::SineImpl_SW(std::vector<std::string> params) : SineSoundComponent(params){ }

void SineImpl_SW::init() { }

void SineImpl_SW::process() {

	std::cout << "Processing sine sw sound component" << std::endl;

	BufferedLink* valueLink = getOutport(SineSoundComponent::sineValueOutPort)->getBufferedLink();
	BufferedLink* frequencyLink = getInport(SineSoundComponent::frequencyInPort)->getBufferedLink();

	int* targetBuffer = (int*) valueLink->getWriteBuffer();
	float* sourceBuffer = (float*) frequencyLink->getReadBuffer();

	int targetBufferSize = valueLink->getBufferDepth();

	float phase_incr = 0.0;

	for (int i = 0; i < targetBufferSize / 4; i++) {

		phase_incr = getPhaseIncrement(sourceBuffer[i]);

		targetBuffer[i] = sin(phase) * INT_MAX;

		phase += phase_incr;

		if (phase >= M_PI * 2) {
			phase -= M_PI * 2;
		}

		std::cout << targetBuffer[i];
	}

}

