XILINX_BIN_PATH=/opt/Xilinx/14.6/ISE_DS/EDK/gnu/arm/lin/bin

ifeq ($(debugging), on)
DEBUGLEVEL = -g3 -O0
else
DEBUGLEVEL = -g0 -O3
endif

CXX=g++
CC_ARM=$(XILINX_BIN_PATH)/arm-xilinx-linux-gnueabi-g++
CXXFLAGS=-Wall -Wno-unused-variable $(DEBUGLEVEL) -fPIC -isystem"../../../Libraries/x86_64/include"
CFLAGS_ARM=-Wall -rdynamic -Wno-unused-variable $(DEBUGLEVEL) -DZYNQ -fPIC -isystem"../../../Libraries/arm/include" 

ifndef target
target=x86_64
endif

ifeq ($(target), arm)		# For Zynq
@echo 'Setting target platform: arm'
CC=$(CC_ARM)
CFLAGS=$(CFLAGS_ARM)
else ifeq ($(target), x86_64)	# For host pc platform
CFLAGS=$(CXXFLAGS)
CC=$(CXX)
endif

CLINKER_FLAGS=-g3 -shared -L"../../../Libraries/$(target)/lib"

SYNTHSIZER_CORE_LIB=../../libsynthesizercore

# Common compile rule
%.o: %.cpp
#	@echo 'Compiling for target $(target)'
#	@echo 'Fallback $(FALLBACK)'
	$(CC) $(CFLAGS) -I$(SYNTHSIZER_CORE_LIB)/include -c $< -o $@
	
%.o: impl/%.cpp
	$(CC) $(CFLAGS) -I$(SYNTHSIZER_CORE_LIB)/include -c $< -o $@
	
clean:
	@rm -f *.so *.o
	 
