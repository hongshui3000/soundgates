/*
 * Main.c
 *
 *  Created on: 05.11.2013
 *      Author: gwue
 */

#include <stdio.h>
#include "Samplebuffer.h"

int main() {


	soundbuffer* playback = buffer_initialize(44100,0);
	//buffer_test_playback(playback);



	printf("Start\n");
	buffer_start(playback,1);
/*	// Fill the buffer thread with random data
  	char mydata[16384];
  	int buffersFilled;
	int i;
	for (buffersFilled =0; playback->running && buffersFilled < 1000;buffersFilled++) {
		for (i= 0; i < 1024*4; i++) {
			mydata[i] = rand();
		}

		while(!buffer_needsamples(playback) && playback->running) {

		}
		buffer_fillbuffer(playback,mydata,1024*4);
	}
*/

	// Synthesizer Test
	initializeComponentsTest();
	runSyntheziserTest(playback);

	buffer_stop(playback);
	printf("Stop\n");

	buffer_free(playback);
	return 0;
}
