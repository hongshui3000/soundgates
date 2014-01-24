--  ____                        _             _            
-- / ___|  ___  _   _ _ __   __| | __ _  __ _| |_ ___  ___ 
-- \___ \ / _ \| | | | '_ \ / _` |/ _` |/ _` | __/ _ \/ __|
--  ___) | (_) | |_| | | | | (_| | (_| | (_| | ||  __/\__ \
-- |____/ \___/ \__,_|_| |_|\__,_|\__, |\__,_|\__\___||___/
--                                |___/                    
-- ======================================================================
--
--   title:        VHDL module - ramp.vhd
--
--   project:      PG-Soundgates
--   author:       Hendrik Hangmann, University of Paderborn
--
--   description:  Ramp filter / envelope
--
-- ======================================================================
    
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.NUMERIC_STD.ALL;
use IEEE.MATH_REAL.ALL;

library soundgates_v1_00_a;
use soundgates_v1_00_a.soundgates_common_pkg.all;

entity ramp is
port(                
        clk     : in  std_logic;
        rst     : in  std_logic;
        ce      : in  std_logic;
        incr    : in  signed(31 downto 0);         
		  incr2   : in  signed(31 downto 0);         
		  rmp     : out signed(31 downto 0)
    );

end ramp;

architecture Behavioral of ramp is

    signal direction : std_logic := '0';

    type states is (s_increasing, s_decreasing, s_exit);
	 signal state : states := s_increasing;

    signal x        : signed (31 downto 0) := to_signed(integer(real( 0.0 * 2**SOUNDGATE_FIX_PT_SCALING)), 32);

    constant upper  : signed (31 downto 0) := to_signed(integer(real( 1.0 * 2**SOUNDGATE_FIX_PT_SCALING)), 32);
    constant lower  : signed (31 downto 0) := to_signed(integer(real( 0.0 * 2**SOUNDGATE_FIX_PT_SCALING)), 32);
--	constant add    : signed (31 downto 0) := to_signed(integer(real(0.02 * 2**SOUNDGATE_FIX_PT_SCALING)), 32);
        
    constant divider: integer   := 100_000;
    signal count    : integer   := 0;

    signal i_clk    : std_logic := '0';
		  
	begin
		  
        rmp <= x;
          
        CALC_RAMP : process (clk, x, incr, rst)
        begin
            if rst = '1' then
                x <= to_signed(integer(real( 0.0 * 2**SOUNDGATE_FIX_PT_SCALING)), 32);
            else
            if rising_edge(clk) then
                if ce = '1' then
                    
						  case state is
						      when s_increasing =>
								   x <= x + incr;
									if x > upper then
										 state <= s_decreasing;
									end if;
                        when s_decreasing => 
                            x <= x - incr2;
                            if x < lower then
                                state <= s_exit;
                            end if; 
							  when s_exit =>
									--
							  end case;
                    
                    end if;
                end if;
				end if;
        end process;  
	  
        
end Behavioral;