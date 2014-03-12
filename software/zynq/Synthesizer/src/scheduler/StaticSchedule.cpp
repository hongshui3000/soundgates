/*
 * StaticSchedule.cpp
 *
 *  Created on: Mar 5, 2014
 *      Author: lukas
 */

#include "StaticSchedule.h"

StaticSchedule::StaticSchedule()
{

    m_nScheduledNodes   = 0;
    m_Time              = 0;

}

StaticSchedule::~StaticSchedule() {

}

void StaticSchedule::schedule(std::vector<NodePtr>& nodes, std::size_t timeslice){

    BOOST_FOREACH(NodePtr node, nodes){

        if(node->getType() == Node::INTERMEDIATE){
            m_nScheduledNodes++;
        }
    }
    std::vector<NodePtr>& slice = (operator [](timeslice));

    slice.insert(m_ScheduleTable[timeslice].begin(),
                        nodes.begin(),
                        nodes.end());

}

uint32_t StaticSchedule::getNumberOfScheduledNodes(){

    return m_nScheduledNodes;

}

void StaticSchedule::printScheduleTable() {

    int time = 0;

    BOOST_FOREACH(std::vector<NodePtr> timeslice, m_ScheduleTable) {
        std::cout << time << ": ";
        BOOST_FOREACH(NodePtr node, timeslice) {

            std::cout << node->getUid() << ", ";
        }
        std::cout << std::endl;

        time++;
    }

}

std::vector<NodePtr>& StaticSchedule::operator[](std::size_t nIndex){

    if(nIndex < (m_ScheduleTable.size() + 1)){
        while((nIndex + 1) - m_ScheduleTable.size() > 0){

            m_ScheduleTable.push_back(std::vector<NodePtr>());
        }
    }

    return m_ScheduleTable[nIndex];
}

bool StaticSchedule::isSchduled(NodePtr node){

    BOOST_FOREACH(std::vector<NodePtr> timeslice, m_ScheduleTable){

        BOOST_FOREACH(NodePtr nodeiter, timeslice){

            if((*node) == (*nodeiter)){
                return true;
            }
        }
    }

    return false;
}

void StaticSchedule::remove(std::size_t attime){

    m_ScheduleTable.erase(m_ScheduleTable.begin() + attime);

}

bool StaticSchedule::isReady(NodePtr node){

    if(isSchduled(node)){
        return false;
    }else{

    switch (node->getType()) {

        case Node::MASTER_SOURCE:
            return true;
        case Node::MASTER_SINK:
            return false;
        case Node::INTERMEDIATE:

            /* Check if all predecessor node were already scheduled */
            BOOST_FOREACH(LinkPtr link, node->getLinks(Link::IN)) {

                NodePtr source = link->getSource();

                if (!isSchduled(source)) {
                    return false;
                }
            }
        }

        return true;

    }
}

void StaticSchedule::timerInterrupt(const boost::system::error_code& e,     /* error code        */
                                    boost::asio::deadline_timer* t,         /* timer object      */
                                    boost::asio::io_service* io,            /* io service object */
                                    Patch::info_t* patchinfo){

    static size_t   intrc = 0;
    static uint64_t ttacc = 0;
    const  int32_t  nintr = (Synthesizer::config::samplerate  / Synthesizer::config::blocksize);
    const  int32_t  interrupt_interval = (1000 * 1000 / nintr) ;

    intrc++;

    if(intrc > nintr){
//        LOG_INFO("Task call count: " << intrc << "x");
//        LOG_INFO("Avg. turnaround time: " << ttacc / intrc);

        patchinfo->turnaround        = ttacc / intrc;
        patchinfo->taskcallcount    += intrc;

        intrc = ttacc = 0;
    }

    t->expires_at(t->expires_at() + boost::posix_time::microseconds(interrupt_interval));

    t->async_wait(boost::bind(&StaticSchedule::timerInterrupt, this,
                      boost::asio::placeholders::error, t, io, patchinfo));



    std::list<boost::unique_future<void> >  pending;

    typedef boost::packaged_task<void>    task_t;
    typedef boost::shared_ptr<task_t>     task_t_ptr;

    using namespace boost::chrono;
    thread_clock::time_point start = thread_clock::now();

    /* On timer event start with the first time slice */
    BOOST_FOREACH(std::vector<NodePtr> timeslice, m_ScheduleTable){
        BOOST_FOREACH(NodePtr node, timeslice) {

                task_t_ptr task = boost::make_shared<task_t>(boost::bind(
                                    &SoundComponent::run,
                                    boost::static_pointer_cast<SoundComponent>(node.get())
                                    ));

                boost::unique_future<void> fut(task->get_future());

                pending.push_back(boost::move(fut));
                io->post(boost::bind(&task_t::operator(), task));
        }

//        boost::wait_for_all(pending.begin(), pending.end());
//        pending.clear();

        while (!pending.empty())
        {
            pending.front().wait();
            pending.pop_front();
        }
    }

    thread_clock::time_point stop = thread_clock::now();
    int_least64_t tt = duration_cast<microseconds>(stop - start).count();

    ttacc += tt;
}
