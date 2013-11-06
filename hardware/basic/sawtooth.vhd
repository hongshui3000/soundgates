library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.NUMERIC_STD.ALL;
use IEEE.MATH_REAL.ALL;


entity sawtooth is
port (		
		clk		: in  std_logic;
		calc 	: in  std_logic;
		offset 	: in  signed(31 downto 0);  
		incr 	: in  signed(31 downto 0); -- negative incr for reversed ramp/sawtooth
		saw 	: out signed(31 downto 0)
		);

end sawtooth;

architecture Behavioral of sawtooth is

	constant scaling : real := 27.0;	

	signal start	 : signed (31 downto 0) := to_signed(integer(real(-1 * 2 ** scaling)), 32);
	signal thresh	 : signed (31 downto 0) := to_signed(integer(real( 1 * 2 ** scaling)), 32);
	signal x		 : signed (31 downto 0) := to_signed(integer(real( 0			   )), 32);
	
	constant pi		 : signed (31 downto 0) := to_signed(integer(real(	    MATH_PI * 2**scaling)), 32);
	constant two_pi  : signed (31 downto 0) := to_signed(integer(real(2.0 * MATH_PI * 2**scaling)), 32);
	constant r_divpi : real 			    := 0.31830988618;
	constant divpi	 : signed (31 downto 0) := to_signed(integer(real(r_divpi * 2**scaling)), 32);
    constant upper   : signed (31 downto 0) := to_signed(integer(real( 1 * 2**scaling)), 32);
    constant lower   : signed (31 downto 0) := to_signed(integer(real(-1 * 2**scaling)), 32);
	
	signal phase	 : signed (31 downto 0) := to_signed(integer(real(0)), 32);
    signal temp	     : signed (31 downto 0);
	
	-- f(0) 	--> -1
	-- f(pi) 	-->  0
	-- f(2 pi)  -->  1
	
	CALC_SAW : process (clk, offset, incr, calc)
	begin
		if rising_edge(clk) then
			if calc = '1' then
				phase <= to_signed(integer(real(x * incr + offset)));
				
				temp <= to_signed(integer(real(divpi * phase - 1)), 32);  -- function for calculating ramp/saw
                
                if temp > upper then
                    saw <= lower;
                    x   <= to_signed(integer(real( 1 * 2**scaling)), 32);
                else
                    saw <= temp;
                end if;
                
				x <= x + 1;

			end if;
		enf if;
	end process;	
	
end Behavioral;

	