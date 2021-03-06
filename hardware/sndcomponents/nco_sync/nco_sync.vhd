--  ____                        _             _            
-- / ___|  ___  _   _ _ __   __| | __ _  __ _| |_ ___  ___ 
-- \___ \ / _ \| | | | '_ \ / _` |/ _` |/ _` | __/ _ \/ __|
--  ___) | (_) | |_| | | | | (_| | (_| | (_| | ||  __/\__ \
-- |____/ \___/ \__,_|_| |_|\__,_|\__, |\__,_|\__\___||___/
--                                |___/                    
-- ======================================================================
--
--   title:        VHDL module - nco_sync.vhd
--
--   project:      PG-Soundgates
--   author:       Hendrik Hangmann, University of Paderborn
--
--   description:  Synchronization of two oscillators
--                 Whenever the master's phase ends, reset slave's phase.
--                 Slave's frequency usually higher and not dividable by
--                 master's frequency
--
-- ======================================================================
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.MATH_REAL.ALL;
use IEEE.NUMERIC_STD.ALL;

library soundgates_v1_00_a;
use soundgates_v1_00_a.soundgates_common_pkg.all;

entity nco_sync is
	 generic(
		FPGA_FREQUENCY : integer := 100_000_000;
		WAVEFORM       : WAVEFORM_TYPE := SAW
	 );
    Port ( 
            clk    : in  std_logic;           
            rst    : in  std_logic;
            ce     : in  std_logic;
            master_phase_offset : in signed(31 downto 0);
            master_phase_incr   : in signed(31 downto 0);
            slave_phase_offset : in signed(31 downto 0);
            slave_phase_incr   : in signed(31 downto 0);
            soundout         : out signed(31 downto 0)
           );
end nco_sync;

architecture Behavioral of nco_sync is 
    component sawtooth
    port(
        clk     : in std_logic;
        ce      : in std_logic;
        rst     : in  std_logic;
        incr    : in signed(31 downto 0);
        offset  : in signed(31 downto 0);
        saw     : out signed(31 downto 0)
    );
    end component sawtooth;
	 
     component nco
     generic(
        FPGA_FREQUENCY : integer := 100_000_000;
        WAVEFORM_SLAVE : WAVEFORM_TYPE := WAVEFORM
	 );
    Port ( 
            clk    : in  std_logic;           
            rst    : in  std_logic;
            ce     : in  std_logic;
            phase_offset : in signed(31 downto 0);
            phase_incr   : in signed(31 downto 0);
            data         : out signed(31 downto 0)
           );
end component nco;
	 
    constant master_threshold  : signed (31 downto 0) := to_signed(integer(real(0.0 * 2**SOUNDGATE_FIX_PT_SCALING)), 32);	
    signal master_data   : signed(31 downto 0);
	 signal slave_data   : signed(31 downto 0);
    signal slave_rst     : std_logic := '0';
    signal state         : integer := 0;

begin
   
    soundout <= slave_data;

    SAWTOOTH_MASTER_INSTA : sawtooth
        port map(                
                    clk     => clk, 
                    ce      => ce,
                    rst     => rst,
                    incr    => master_phase_incr, 
                    offset  => master_phase_offset,
                    saw     => master_data ); 
	
    NCO_INSTA : nco
    Port map( 
            clk    => clk,         
            rst    => slave_rst,
            ce     => ce,
            phase_offset => slave_phase_offset,
            phase_incr   => slave_phase_incr,
            data         => slave_data
           );
			  
    SYNC_PROCESS : process (clk)
	 begin
	     if rising_edge(clk) then
		      if master_data = master_threshold then
				    case state is
					 when 0 => 
						  slave_rst <= '1';
						  state <= 1;
					 when 1 =>
						  slave_rst <= '0';
						  state <= 0;
					 when others => 
						  state <= 0;
					 end case;
				elsif slave_rst = '1' then
					slave_rst <= '0';
				end if;
		  end if;
	 end process;
	
end Behavioral;
