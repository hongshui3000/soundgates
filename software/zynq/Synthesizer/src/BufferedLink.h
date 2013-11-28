/*
 * SoundLink.h
 *
 *  Created on: Nov 27, 2013
 *      Author: lukas
 */

#ifndef BUFFEREDLINK_H_
#define BUFFEREDLINK_H_

#include "Link.h"


class BufferedLink : public Link {

private:

	char* readbuffer;
	char* writebuffer;
	int bufferdepth;

public:

	BufferedLink(Node *source, Node* dest, int bufferdepth);
	~BufferedLink(void);

	void switchBuffers(void);

	char* getReadBuffer(void);
	char* getWriteBuffer(void);
	int getBufferDepth(void);

};


#endif /* SOUNDLINK_H_ */
