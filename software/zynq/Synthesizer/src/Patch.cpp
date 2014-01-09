/*
 * Patch.cpp
 *
 *  Created on: Nov 27, 2013
 *      Author: lukas
 */


#include "Patch.h"

extern Patch* patch;

Patch::Patch(){

	m_InputComponents = NULL;
	m_PatchState  	  = Synthesizer::state::created;
	m_SndComponentBarrier = NULL;
}

Patch::~Patch(){ }


const vector<InputSoundComponent*>& Patch::getInputSoundComponents(){

	if(NULL == m_InputComponents){

		m_InputComponents = new vector<InputSoundComponent*>;

		for(vector<SoundComponent*>::iterator iter = m_ComponentsVector.begin(); iter != m_ComponentsVector.end(); ++iter){

			SoundComponentImpl* sndcomponent = (*iter)->getDelegate();

			LOG_DEBUG("Check for input sound component: " << typeid(*sndcomponent).name());

			if(typeid(*sndcomponent) == typeid(InputSoundComponent)){

				m_InputComponents->push_back(dynamic_cast<InputSoundComponent*>(sndcomponent));
			}
		}
	}

	return *m_InputComponents;
}

void Patch::createSoundComponent(int uid, std::string type, std::vector<std::string> parameters, int slot){

	SoundComponents::ImplType impltype;

	SoundComponentLoader& loader = SoundComponentLoader::getInstance();

	impltype = (slot < 0) ? SoundComponents::SW : SoundComponents::HW;

	SoundComponentImpl* impl = loader.createFromString(type, impltype, parameters);

	if(NULL != impl){
		SoundComponent* component = new SoundComponent(uid, impl);

		LOG_DEBUG("Adding component to patch uid: " << dynamic_cast<Node*>(component)->getUid() << " at " << component);
		LOG_DEBUG("Component #inports : " << component->getInports().size());
		LOG_DEBUG("Component #outorts : " << component->getOutports().size());

		m_ComponentsVector.push_back(component);
	}
}


void Patch::createLink(int sourceid, int srcport, int destid, int destport){

	LOG_DEBUG("Creating sound link from node " << sourceid << " to Node " << destid);

	SoundComponent* source;
	SoundComponent* destination;

	for(vector<SoundComponent*>::iterator iter = m_ComponentsVector.begin(); iter != m_ComponentsVector.end(); ++iter){

		if( ((Node*)*iter)->getUid() == sourceid){
			source = *iter;
		}
		if( ((Node*)*iter)->getUid() == destid) {
			destination = *iter;
		}
	}

	LOG_DEBUG("Source " << source << " Destination " << destination);

	Port* srcPort  = source->getDelegate()->getOutport(srcport);
    Port* destPort = destination->getDelegate()->getInport(destport);

    Link* link = NULL;

    if(typeid(*srcPort) != typeid(*destPort)){

        LOG_ERROR("Sourceport of type " << typeid(*srcPort).name() << " does not match destinationport of type " << typeid(*destPort).name());

    }else{


        /*
         * Check if src has already an outgoid connection
         */
        if(srcPort->getLink() != NULL){

            destination->addIncomingLink(*(srcPort->getLink()), destport);

        }else if(destPort->getLink() != NULL){  /* Check if destination port has already been linked */

            LOG_ERROR("Destination port of component " << destid << "already connected");

        }else{

            LOG_DEBUG("Source port type"      << typeid(*srcPort).name());
            LOG_DEBUG("Destination port type" << typeid(*destPort).name());

            /* Create SoundLink */
            if(typeid(*srcPort) == typeid(SoundPort)){

                LOG_DEBUG("Creating buffered link");
                link = new BufferedLink((Node*)source, (Node*)destination, Synthesizer::config::blocksize * sizeof(int));

                m_BufferedLinksVector.push_back((BufferedLink*)link);

            /* Create ControlLink */
            }else if(typeid(*srcPort) == typeid(ControlPort)){

                LOG_DEBUG("Creating control link");
                link = new ControlLink((Node*)source, (Node*)destination);

                m_ControlLinksVector.push_back((ControlLink*)link);
            }

            if(NULL == link) {

                LOG_ERROR("Could allocate create link object");
            } else {

                source->addOutgoingLink(*link, srcport);
                destination->addIncomingLink(*link, destport);

            }
        }
    }
}

void Patch::initialize(void){


	for(vector<SoundComponent*>::iterator iter = m_ComponentsVector.begin(); iter != m_ComponentsVector.end(); ++iter ){

		(*iter)->init();
	}

	this->m_PatchState = Synthesizer::state::initialized;

}

void Patch::run(){


	if(Synthesizer::state::initialized == m_PatchState){

		m_PatchState = Synthesizer::state::running;

		//TODO: Make sure that processing time in both workers is nearly equal

		vector<SoundComponent*> split_lo(m_ComponentsVector.begin(), m_ComponentsVector.begin() + m_ComponentsVector.size() / 2);

		vector<SoundComponent*> split_hi(m_ComponentsVector.begin() + m_ComponentsVector.size() / 2, m_ComponentsVector.end());

		boost::condition_variable_any* buffersync 	 = new boost::condition_variable_any;
		boost::condition_variable_any* componentsync = new boost::condition_variable_any;

		boost::shared_mutex sync;


		SoundComponentWorker w1(m_ComponentsVector, &sync, buffersync, componentsync);

// TODO: Check why this leads to noise

//		SoundComponentWorker w1(split_lo, &sync, buffersync, componentsync);
//		SoundComponentWorker w2(split_hi, &sync, buffersync, componentsync);

		SoundLinkWorker w3(m_BufferedLinksVector, &sync, componentsync, buffersync);

		boost::thread componentworkerthread_t1(boost::ref(w1));
		//boost::thread componentworkerthread_t2(boost::ref(w2));

		boost::thread linkworkerthread_t1(boost::ref(w3));

		while(1){
			usleep(1000);
		}
	}
}

void Patch::switchBuffers(){

	for (vector<BufferedLink*>::iterator iter = m_BufferedLinksVector.begin();
			iter != m_BufferedLinksVector.end(); ++iter) {

		static_cast<BufferedLink*>((*iter))->switchBuffers();
	}
}

void Patch::stop(){

	if(Synthesizer::state::running == m_PatchState){

		m_PatchState = Synthesizer::state::stopped;

		//TODO: currently not implemented
	}
}
