/*
 * Or_SW.cpp
 *
 *  Created on: Feb 23, 2014
 *      Author: posewsky
 */

#include "Or_SW.hpp"

Or_SW::Or_SW(std::vector<std::string> params) : Or(params)
{
	// implementation specific initialization stuff here
}

// The actual software processing
// In this example, we take an incoming control value and write it to each sample on the sound output
// and take an incoming sound signal, average over the samples and write it to the control output
void Or_SW::process()
{
	bool out = (in0>0) || (in1>0);
	if(out == last_out)
		return;

	last_out = out;
	m_Out_1_Port->push(out ? 1.0f : 0.0f);
}