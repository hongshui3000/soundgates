/*
 * RecordingSoundComponent.h
 *
 *  Created on: Feb 27, 2013
 *      Author: gwue
 *
 */

#ifndef RECORDINGSOUNDCOMPONENT_H_
#define RECORDINGSOUNDCOMPONENT_H_

#include <string>
#include <vector>

#include <Synthesizer.h>
#include <SoundComponentImpl.h>
#include <SoundPort.h>

#include "Soundbuffer.hpp"

// TODO Remove these defines when merged with the other project
#ifndef SAMPLE_SIZE
#define SAMPLE_SIZE        sizeof(int)
#endif

#ifndef SAMPLE_RATE
#define SAMPLE_RATE        44100
#endif

#ifndef SAMPLE_COUNT
#define SAMPLE_COUNT       64
#endif

using namespace std;

class RecordingSoundComponent: public SoundComponentImpl
{

private:
	Soundbuffer* buffer;
public:

	DECLARE_COMPONENTNAME

	DECLARE_PORT3(SoundPort, SoundOut, 1);

	RecordingSoundComponent(std::vector<std::string> params);
	virtual ~RecordingSoundComponent();

	void init(void);
	void process(void);

};

#endif /* OUTPUTSOUNDCOMPONENT_H_ */
