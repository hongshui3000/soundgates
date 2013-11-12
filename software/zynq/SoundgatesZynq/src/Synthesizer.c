/*
 * Synthesizer.c
 *
 *  Created on: 06.11.2013
 *      Author: soundgates
 */

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <fcntl.h>
#include <unistd.h>
#include <math.h>

// Reconos imports
//#include </home/soundgates/zynq/reconos_zynq/lib/include/reconos.h>
//#include </home/soundgates/zynq/reconos_zynq/lib/include/mbox.h>

#include <reconos.h>
#include <mbox.h>
// Soundgates imports
#include "ComponentStructs.h"

// Defines
#define SAMPLE_COUNT 64
#define SAMPLE_SIZE  sizeof(int)
#define MBOX_SIZE 1
// One Array for every SW thread
//pthread_t swt[SW_THREADS];
//pthread_attr_t swt_attr[SW_THREADS];

// One array for every HW thread
#define HW_THREADS 1 // -- Generate
struct reconos_hwt hwt[HW_THREADS];
struct reconos_resource hw_res[2];

// Define HW dependent variables -- Generate
int component_lvl1_sine_offset;
int component_lvl1_sine_increment;
int* component_lvl1_sine_targetbufer;

// Define Message Boxes
struct mbox  mb_start;
struct mbox  mb_stop;

void initializeComponents()
{
	// init mailboxes
	mbox_init(&mb_start,MBOX_SIZE);
    mbox_init(&mb_stop ,MBOX_SIZE);
    // init reconos and communication resources
    reconos_init();
    hw_res[0].type = RECONOS_TYPE_MBOX;
    hw_res[0].ptr  = &mb_start;
    hw_res[1].type = RECONOS_TYPE_MBOX;
    hw_res[1].ptr  = &mb_stop;

    int i;
    // Set resources (MBOX)
    for(i=0; i<HW_THREADS; i++)
    {
    	reconos_hwt_setresources(&(hwt[i]),hw_res,2);
    }

    // Define structs, variables & allocate memory
    // i.e. sine_component
	component_lvl1_sine_targetbufer = malloc(sizeof(SAMPLE_SIZE)*SAMPLE_COUNT);
	component_lvl1_sine_offset      = 20; // Start offset
    sBase sine_base = {SAMPLE_COUNT, component_lvl1_sine_targetbufer};
    sHeaderSine component_lvl1_sine_header = {sine_base, &component_lvl1_sine_offset};

	reconos_hwt_setinitdata(&hwt[0], (void *)&component_lvl1_sine_header);
//	reconos_hwt_setinitdata(&hwt[1], (void *)&component_lvl1_sawtooth_header);

    // Create HW Threads
	for(i=0; i<HW_THREADS; i++)
	{
		reconos_hwt_create(&hwt[i],i,NULL);
	}

//	// init software threads
//	printf("Creating %i sw-threads: ",sw_threads);
//	fflush(stdout);
//	for (i = 0; i < sw_threads; i++)
//	{
//	  printf(" %i",i);fflush(stdout);
//	  pthread_attr_init(&swt_attr[i]);
//	  pthread_create(&swt[i], &swt_attr[i], sort_thread, (void*)res);
//	}

}

int main(int argc, char ** argv)
{
    void initializeComponents();

    while(1)
    {
        // Start Layer 1

        // If layer1 ready --> start layer 2
//    	printf("Waiting for termination...\n");
//    	for (i=0; i<hw_threads; i++)
//    	{
//    	  pthread_join(hwt[i].delegate,NULL);
//    	}
//    	for (i=0; i<sw_threads; i++)
//    	{
//    	  pthread_join(swt[i],NULL);
//    	}

        // start layer n

        // Get user input - async --> done by another thread
        // write new values depending on user input into memory
    }

}
