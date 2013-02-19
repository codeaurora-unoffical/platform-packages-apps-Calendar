package com.android.calendar.lunar;

import android.content.Context;
import com.android.calendar.R;

/**
 * 
 * @author Administrator
 *
 */
public class ToLunarCalendar 
{
	private static final int START_YEAR = 1901;  //from 1901
	private static final int END_YEAR   = 2050;  //to 2050
	
	// 保存转换的农历日期
	private int mLunarYear;
	private int mLunarMonth;
	private int mLunarDay;
	
	// 保存要转换的阳历日期
	private int mYear;
	private int mMonth;
	private int mDay;
	
	
	
	//阴历每月只能是29或30天，一年用12（或13）个二进制位表示，对应位为1表30天，否则为29天
	private static final int gLunarMonthDay[]=  
	{  
	  // 1901.1.1 --2050.12.31  
	  0X4ae0, 0Xa570, 0X5268, 0Xd260, 0Xd950, 0X6aa8, 0X56a0, 0X9ad0, 0X4ae8, 0X4ae0,   //1910  
	  0Xa4d8, 0Xa4d0, 0Xd250, 0Xd548, 0Xb550, 0X56a0, 0X96d0, 0X95b0, 0X49b8, 0X49b0,   //1920  
	  0Xa4b0, 0Xb258, 0X6a50, 0X6d40, 0Xada8, 0X2b60, 0X9570, 0X4978, 0X4970, 0X64b0,   //1930  
	  0Xd4a0, 0Xea50, 0X6d48, 0X5ad0, 0X2b60, 0X9370, 0X92e0, 0Xc968, 0Xc950, 0Xd4a0,   //1940  
	  0Xda50, 0Xb550, 0X56a0, 0Xaad8, 0X25d0, 0X92d0, 0Xc958, 0Xa950, 0Xb4a8, 0X6ca0,   //1950  
	  0Xb550, 0X55a8, 0X4da0, 0Xa5b0, 0X52b8, 0X52b0, 0Xa950, 0Xe950, 0X6aa0, 0Xad50,   //1960  
	  0Xab50, 0X4b60, 0Xa570, 0Xa570, 0X5260, 0Xe930, 0Xd950, 0X5aa8, 0X56a0, 0X96d0,   //1970  
	  0X4ae8, 0X4ad0, 0Xa4d0, 0Xd268, 0Xd250, 0Xd528, 0Xb540, 0Xb6a0, 0X96d0, 0X95b0,   //1980  
	  0X49b0, 0Xa4b8, 0Xa4b0, 0Xb258, 0X6a50, 0X6d40, 0Xada0, 0Xab60, 0X9570, 0X4978,   //1990  
	  0X4970, 0X64b0, 0X6a50, 0Xea50, 0X6b28, 0X5ac0, 0Xab60, 0X9368, 0X92e0, 0Xc960,   //2000  
	  0Xd4a8, 0Xd4a0, 0Xda50, 0X5aa8, 0X56a0, 0Xaad8, 0X25d0, 0X92d0, 0Xc958, 0Xa950,   //2010  
	  0Xb4a0, 0Xb550, 0Xad50, 0X55a8, 0X4ba0, 0Xa5b0, 0X52b8, 0X52b0, 0Xa930, 0X74a8,   //2020  
	  0X6aa0, 0Xad50, 0X4da8, 0X4b60, 0Xa570, 0Xa4e0, 0Xd260, 0Xe930, 0Xd530, 0X5aa0,   //2030  
	  0X6b50, 0X96d0, 0X4ae8, 0X4ad0, 0Xa4d0, 0Xd258, 0Xd250, 0Xd520, 0Xdaa0, 0Xb5a0,   //2040  
	  0X56d0, 0X4ad8, 0X49b0, 0Xa4b8, 0Xa4b0, 0Xaa50, 0Xb528, 0X6d20, 0Xada0, 0X55b0,   //2050  
	};  
	
	//农历闰月的月份
	private static final char gLunarMonth[]=  
	{  
		0X00, 0X50, 0X04, 0X00, 0X20,   //1910  
		0X60, 0X05, 0X00, 0X20, 0X70,   //1920  
		0X05, 0X00, 0X40, 0X02, 0X06,   //1930  
		0X00, 0X50, 0X03, 0X07, 0X00,   //1940  
		0X60, 0X04, 0X00, 0X20, 0X70,   //1950  
		0X05, 0X00, 0X30, 0X80, 0X06,   //1960  
		0X00, 0X40, 0X03, 0X07, 0X00,   //1970  
		0X50, 0X04, 0X08, 0X00, 0X60,   //1980  
		0X04, 0X0a, 0X00, 0X60, 0X05,   //1990  
		0X00, 0X30, 0X80, 0X05, 0X00,   //2000  
		0X40, 0X02, 0X07, 0X00, 0X50,   //2010  
		0X04, 0X09, 0X00, 0X60, 0X04,   //2020  
		0X00, 0X20, 0X60, 0X05, 0X00,   //2030  
		0X30, 0Xb0, 0X06, 0X00, 0X50,   //2040  
		0X02, 0X07, 0X00, 0X50, 0X03    //2050  
	};  

    //阴历每月只能是29或30天，一年用12（或13）个二进制位表示，对应位为1表30天，否则为29天  
    //数组gLanarHoliday存放每年的二十四节气对应的阳历日期  
    //每年的二十四节气对应的阳历日期几乎固定，平均分布于十二个月中  

    //   1月		2月  	 3月     	4月   	 5月      	6月  
    //小寒 大寒   立春  雨水   惊蛰 春分   清明 谷雨   立夏 小满   芒种 夏至  

    //   7月     	8月   	 9月       	 10月       11月      	12月  
    //小暑 大暑   立秋  处暑   白露 秋分   寒露 霜降   立冬 小雪   大雪 冬至
	private static final char gLunarHolDay[]=  
	{  
		0X96, 0XB4, 0X96, 0XA6, 0X97, 0X97, 0X78, 0X79, 0X79, 0X69, 0X78, 0X77,   //1901  
		0X96, 0XA4, 0X96, 0X96, 0X97, 0X87, 0X79, 0X79, 0X79, 0X69, 0X78, 0X78,   //1902  
		0X96, 0XA5, 0X87, 0X96, 0X87, 0X87, 0X79, 0X69, 0X69, 0X69, 0X78, 0X78,   //1903  
		0X86, 0XA5, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X79, 0X78, 0X87,   //1904  
		0X96, 0XB4, 0X96, 0XA6, 0X97, 0X97, 0X78, 0X79, 0X79, 0X69, 0X78, 0X77,   //1905  
		0X96, 0XA4, 0X96, 0X96, 0X97, 0X97, 0X79, 0X79, 0X79, 0X69, 0X78, 0X78,   //1906  
		0X96, 0XA5, 0X87, 0X96, 0X87, 0X87, 0X79, 0X69, 0X69, 0X69, 0X78, 0X78,   //1907  
		0X86, 0XA5, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X69, 0X78, 0X87,   //1908  
		0X96, 0XB4, 0X96, 0XA6, 0X97, 0X97, 0X78, 0X79, 0X79, 0X69, 0X78, 0X77,   //1909  
		0X96, 0XA4, 0X96, 0X96, 0X97, 0X97, 0X79, 0X79, 0X79, 0X69, 0X78, 0X78,   //1910  
		0X96, 0XA5, 0X87, 0X96, 0X87, 0X87, 0X79, 0X69, 0X69, 0X69, 0X78, 0X78,   //1911  
		0X86, 0XA5, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X69, 0X78, 0X87,   //1912  
		0X95, 0XB4, 0X96, 0XA6, 0X97, 0X97, 0X78, 0X79, 0X79, 0X69, 0X78, 0X77,   //1913  
		0X96, 0XB4, 0X96, 0XA6, 0X97, 0X97, 0X79, 0X79, 0X79, 0X69, 0X78, 0X78,   //1914  
		0X96, 0XA5, 0X97, 0X96, 0X97, 0X87, 0X79, 0X79, 0X69, 0X69, 0X78, 0X78,   //1915  
		0X96, 0XA5, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X79, 0X77, 0X87,   //1916  
		0X95, 0XB4, 0X96, 0XA6, 0X96, 0X97, 0X78, 0X79, 0X78, 0X69, 0X78, 0X87,   //1917  
		0X96, 0XB4, 0X96, 0XA6, 0X97, 0X97, 0X79, 0X79, 0X79, 0X69, 0X78, 0X77,   //1918  
		0X96, 0XA5, 0X97, 0X96, 0X97, 0X87, 0X79, 0X79, 0X69, 0X69, 0X78, 0X78,   //1919  
		0X96, 0XA5, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X79, 0X77, 0X87,   //1920  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X78, 0X79, 0X78, 0X69, 0X78, 0X87,   //1921  
		0X96, 0XB4, 0X96, 0XA6, 0X97, 0X97, 0X79, 0X79, 0X79, 0X69, 0X78, 0X77,   //1922  
		0X96, 0XA4, 0X96, 0X96, 0X97, 0X87, 0X79, 0X79, 0X69, 0X69, 0X78, 0X78,   //1923  
		0X96, 0XA5, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X79, 0X77, 0X87,   //1924  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X78, 0X79, 0X78, 0X69, 0X78, 0X87,   //1925  
		0X96, 0XB4, 0X96, 0XA6, 0X97, 0X97, 0X78, 0X79, 0X79, 0X69, 0X78, 0X77,   //1926  
		0X96, 0XA4, 0X96, 0X96, 0X97, 0X87, 0X79, 0X79, 0X79, 0X69, 0X78, 0X78,   //1927  
		0X96, 0XA5, 0X96, 0XA5, 0X96, 0X96, 0X88, 0X78, 0X78, 0X78, 0X87, 0X87,   //1928  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X79, 0X77, 0X87,   //1929  
		0X96, 0XB4, 0X96, 0XA6, 0X97, 0X97, 0X78, 0X79, 0X79, 0X69, 0X78, 0X77,   //1930  
		0X96, 0XA4, 0X96, 0X96, 0X97, 0X87, 0X79, 0X79, 0X79, 0X69, 0X78, 0X78,   //1931  
		0X96, 0XA5, 0X96, 0XA5, 0X96, 0X96, 0X88, 0X78, 0X78, 0X78, 0X87, 0X87,   //1932  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X69, 0X78, 0X87,   //1933  
		0X96, 0XB4, 0X96, 0XA6, 0X97, 0X97, 0X78, 0X79, 0X79, 0X69, 0X78, 0X77,   //1934  
		0X96, 0XA4, 0X96, 0X96, 0X97, 0X97, 0X79, 0X79, 0X79, 0X69, 0X78, 0X78,   //1935  
		0X96, 0XA5, 0X96, 0XA5, 0X96, 0X96, 0X88, 0X78, 0X78, 0X78, 0X87, 0X87,   //1936  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X69, 0X78, 0X87,   //1937  
		0X96, 0XB4, 0X96, 0XA6, 0X97, 0X97, 0X78, 0X79, 0X79, 0X69, 0X78, 0X77,   //1938  
		0X96, 0XA4, 0X96, 0X96, 0X97, 0X97, 0X79, 0X79, 0X79, 0X69, 0X78, 0X78,   //1939  
		0X96, 0XA5, 0X96, 0XA5, 0X96, 0X96, 0X88, 0X78, 0X78, 0X78, 0X87, 0X87,   //1940  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X69, 0X78, 0X87,   //1941  
		0X96, 0XB4, 0X96, 0XA6, 0X97, 0X97, 0X78, 0X79, 0X79, 0X69, 0X78, 0X77,   //1942  
		0X96, 0XA4, 0X96, 0X96, 0X97, 0X97, 0X79, 0X79, 0X79, 0X69, 0X78, 0X78,   //1943  
		0X96, 0XA5, 0X96, 0XA5, 0XA6, 0X96, 0X88, 0X78, 0X78, 0X78, 0X87, 0X87,   //1944  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X79, 0X77, 0X87,   //1945  
		0X95, 0XB4, 0X96, 0XA6, 0X97, 0X97, 0X78, 0X79, 0X78, 0X69, 0X78, 0X77,   //1946  
		0X96, 0XB4, 0X96, 0XA6, 0X97, 0X97, 0X79, 0X79, 0X79, 0X69, 0X78, 0X78,   //1947  
		0X96, 0XA5, 0XA6, 0XA5, 0XA6, 0X96, 0X88, 0X88, 0X78, 0X78, 0X87, 0X87,   //1948  
		0XA5, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X79, 0X78, 0X79, 0X77, 0X87,   //1949  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X78, 0X79, 0X78, 0X69, 0X78, 0X77,   //1950  
		0X96, 0XB4, 0X96, 0XA6, 0X97, 0X97, 0X79, 0X79, 0X79, 0X69, 0X78, 0X78,   //1951  
		0X96, 0XA5, 0XA6, 0XA5, 0XA6, 0X96, 0X88, 0X88, 0X78, 0X78, 0X87, 0X87,   //1952  
		0XA5, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X79, 0X77, 0X87,   //1953  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X78, 0X79, 0X78, 0X68, 0X78, 0X87,   //1954  
		0X96, 0XB4, 0X96, 0XA6, 0X97, 0X97, 0X78, 0X79, 0X79, 0X69, 0X78, 0X77,   //1955  
		0X96, 0XA5, 0XA5, 0XA5, 0XA6, 0X96, 0X88, 0X88, 0X78, 0X78, 0X87, 0X87,   //1956  
		0XA5, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X79, 0X77, 0X87,   //1957  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X69, 0X78, 0X87,   //1958  
		0X96, 0XB4, 0X96, 0XA6, 0X97, 0X97, 0X78, 0X79, 0X79, 0X69, 0X78, 0X77,   //1959  
		0X96, 0XA4, 0XA5, 0XA5, 0XA6, 0X96, 0X88, 0X88, 0X88, 0X78, 0X87, 0X87,   //1960  
		0XA5, 0XB4, 0X96, 0XA5, 0X96, 0X96, 0X88, 0X78, 0X78, 0X78, 0X87, 0X87,   //1961  
		0X96, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X69, 0X78, 0X87,   //1962  
		0X96, 0XB4, 0X96, 0XA6, 0X97, 0X97, 0X78, 0X79, 0X79, 0X69, 0X78, 0X77,   //1963  
		0X96, 0XA4, 0XA5, 0XA5, 0XA6, 0X96, 0X88, 0X88, 0X88, 0X78, 0X87, 0X87,   //1964  
		0XA5, 0XB4, 0X96, 0XA5, 0X96, 0X96, 0X88, 0X78, 0X78, 0X78, 0X87, 0X87,   //1965  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X69, 0X78, 0X87,   //1966  
		0X96, 0XB4, 0X96, 0XA6, 0X97, 0X97, 0X78, 0X79, 0X79, 0X69, 0X78, 0X77,   //1967  
		0X96, 0XA4, 0XA5, 0XA5, 0XA6, 0XA6, 0X88, 0X88, 0X88, 0X78, 0X87, 0X87,   //1968  
		0XA5, 0XB4, 0X96, 0XA5, 0X96, 0X96, 0X88, 0X78, 0X78, 0X78, 0X87, 0X87,   //1969  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X69, 0X78, 0X87,   //1970  
		0X96, 0XB4, 0X96, 0XA6, 0X97, 0X97, 0X78, 0X79, 0X79, 0X69, 0X78, 0X77,   //1971  
		0X96, 0XA4, 0XA5, 0XA5, 0XA6, 0XA6, 0X88, 0X88, 0X88, 0X78, 0X87, 0X87,   //1972  
		0XA5, 0XB5, 0X96, 0XA5, 0XA6, 0X96, 0X88, 0X78, 0X78, 0X78, 0X87, 0X87,   //1973  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X69, 0X78, 0X87,   //1974  
		0X96, 0XB4, 0X96, 0XA6, 0X97, 0X97, 0X78, 0X79, 0X78, 0X69, 0X78, 0X77,   //1975  
		0X96, 0XA4, 0XA5, 0XB5, 0XA6, 0XA6, 0X88, 0X89, 0X88, 0X78, 0X87, 0X87,   //1976  
		0XA5, 0XB4, 0X96, 0XA5, 0X96, 0X96, 0X88, 0X88, 0X78, 0X78, 0X87, 0X87,   //1977  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X79, 0X78, 0X87,   //1978  
		0X96, 0XB4, 0X96, 0XA6, 0X96, 0X97, 0X78, 0X79, 0X78, 0X69, 0X78, 0X77,   //1979  
		0X96, 0XA4, 0XA5, 0XB5, 0XA6, 0XA6, 0X88, 0X88, 0X88, 0X78, 0X87, 0X87,   //1980  
		0XA5, 0XB4, 0X96, 0XA5, 0XA6, 0X96, 0X88, 0X88, 0X78, 0X78, 0X77, 0X87,   //1981  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X79, 0X77, 0X87,   //1982  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X78, 0X79, 0X78, 0X69, 0X78, 0X77,   //1983  
		0X96, 0XB4, 0XA5, 0XB5, 0XA6, 0XA6, 0X87, 0X88, 0X88, 0X78, 0X87, 0X87,   //1984  
		0XA5, 0XB4, 0XA6, 0XA5, 0XA6, 0X96, 0X88, 0X88, 0X78, 0X78, 0X87, 0X87,   //1985  
		0XA5, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X79, 0X77, 0X87,   //1986  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X79, 0X78, 0X69, 0X78, 0X87,   //1987  
		0X96, 0XB4, 0XA5, 0XB5, 0XA6, 0XA6, 0X87, 0X88, 0X88, 0X78, 0X87, 0X86,   //1988  
		0XA5, 0XB4, 0XA5, 0XA5, 0XA6, 0X96, 0X88, 0X88, 0X88, 0X78, 0X87, 0X87,   //1989  
		0XA5, 0XB4, 0X96, 0XA5, 0X96, 0X96, 0X88, 0X78, 0X78, 0X79, 0X77, 0X87,   //1990  
		0X95, 0XB4, 0X96, 0XA5, 0X86, 0X97, 0X88, 0X78, 0X78, 0X69, 0X78, 0X87,   //1991  
		0X96, 0XB4, 0XA5, 0XB5, 0XA6, 0XA6, 0X87, 0X88, 0X88, 0X78, 0X87, 0X86,   //1992  
		0XA5, 0XB3, 0XA5, 0XA5, 0XA6, 0X96, 0X88, 0X88, 0X88, 0X78, 0X87, 0X87,   //1993  
		0XA5, 0XB4, 0X96, 0XA5, 0X96, 0X96, 0X88, 0X78, 0X78, 0X78, 0X87, 0X87,   //1994  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X76, 0X78, 0X69, 0X78, 0X87,   //1995  
		0X96, 0XB4, 0XA5, 0XB5, 0XA6, 0XA6, 0X87, 0X88, 0X88, 0X78, 0X87, 0X86,   //1996  
		0XA5, 0XB3, 0XA5, 0XA5, 0XA6, 0XA6, 0X88, 0X88, 0X88, 0X78, 0X87, 0X87,   //1997  
		0XA5, 0XB4, 0X96, 0XA5, 0X96, 0X96, 0X88, 0X78, 0X78, 0X78, 0X87, 0X87,   //1998  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X69, 0X78, 0X87,   //1999  
		0X96, 0XB4, 0XA5, 0XB5, 0XA6, 0XA6, 0X87, 0X88, 0X88, 0X78, 0X87, 0X86,   //2000  
		0XA5, 0XB3, 0XA5, 0XA5, 0XA6, 0XA6, 0X88, 0X88, 0X88, 0X78, 0X87, 0X87,   //2001  
		0XA5, 0XB4, 0X96, 0XA5, 0X96, 0X96, 0X88, 0X78, 0X78, 0X78, 0X87, 0X87,   //2002  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X69, 0X78, 0X87,   //2003  
		0X96, 0XB4, 0XA5, 0XB5, 0XA6, 0XA6, 0X87, 0X88, 0X88, 0X78, 0X87, 0X86,   //2004  
		0XA5, 0XB3, 0XA5, 0XA5, 0XA6, 0XA6, 0X88, 0X88, 0X88, 0X78, 0X87, 0X87,   //2005  
		0XA5, 0XB4, 0X96, 0XA5, 0XA6, 0X96, 0X88, 0X88, 0X78, 0X78, 0X87, 0X87,   //2006  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X69, 0X78, 0X87,   //2007  
		0X96, 0XB4, 0XA5, 0XB5, 0XA6, 0XA6, 0X87, 0X88, 0X87, 0X78, 0X87, 0X86,   //2008  
		0XA5, 0XB3, 0XA5, 0XB5, 0XA6, 0XA6, 0X88, 0X88, 0X88, 0X78, 0X87, 0X87,   //2009  
		0XA5, 0XB4, 0X96, 0XA5, 0XA6, 0X96, 0X88, 0X88, 0X78, 0X78, 0X87, 0X87,   //2010  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X79, 0X78, 0X87,   //2011  
		0X96, 0XB4, 0XA5, 0XB5, 0XA5, 0XA6, 0X87, 0X88, 0X87, 0X78, 0X87, 0X86,   //2012  
		0XA5, 0XB3, 0XA5, 0XB5, 0XA6, 0XA6, 0X87, 0X88, 0X88, 0X78, 0X87, 0X87,   //2013  
		0XA5, 0XB4, 0X96, 0XA5, 0XA6, 0X96, 0X88, 0X88, 0X78, 0X78, 0X87, 0X87,   //2014  
		0X95, 0XB4, 0X96, 0XA5, 0X96, 0X97, 0X88, 0X78, 0X78, 0X79, 0X77, 0X87,   //2015  
		0X95, 0XB4, 0XA5, 0XB4, 0XA5, 0XA6, 0X87, 0X88, 0X87, 0X78, 0X87, 0X86,   //2016  
		0XA5, 0XC3, 0XA5, 0XB5, 0XA6, 0XA6, 0X87, 0X88, 0X88, 0X78, 0X87, 0X87,   //2017  
		0XA5, 0XB4, 0XA6, 0XA5, 0XA6, 0X96, 0X88, 0X88, 0X78, 0X78, 0X87, 0X87,   //2018  
		0XA5, 0XB4, 0X96, 0XA5, 0X96, 0X96, 0X88, 0X78, 0X78, 0X79, 0X77, 0X87,   //2019  
		0X95, 0XB4, 0XA5, 0XB4, 0XA5, 0XA6, 0X97, 0X87, 0X87, 0X78, 0X87, 0X86,   //2020  
		0XA5, 0XC3, 0XA5, 0XB5, 0XA6, 0XA6, 0X87, 0X88, 0X88, 0X78, 0X87, 0X86,   //2021  
		0XA5, 0XB4, 0XA5, 0XA5, 0XA6, 0X96, 0X88, 0X88, 0X88, 0X78, 0X87, 0X87,   //2022  
		0XA5, 0XB4, 0X96, 0XA5, 0X96, 0X96, 0X88, 0X78, 0X78, 0X79, 0X77, 0X87,   //2023  
		0X95, 0XB4, 0XA5, 0XB4, 0XA5, 0XA6, 0X97, 0X87, 0X87, 0X78, 0X87, 0X96,   //2024  
		0XA5, 0XC3, 0XA5, 0XB5, 0XA6, 0XA6, 0X87, 0X88, 0X88, 0X78, 0X87, 0X86,   //2025  
		0XA5, 0XB3, 0XA5, 0XA5, 0XA6, 0XA6, 0X88, 0X88, 0X88, 0X78, 0X87, 0X87,   //2026  
		0XA5, 0XB4, 0X96, 0XA5, 0X96, 0X96, 0X88, 0X78, 0X78, 0X78, 0X87, 0X87,   //2027  
		0X95, 0XB4, 0XA5, 0XB4, 0XA5, 0XA6, 0X97, 0X87, 0X87, 0X78, 0X87, 0X96,   //2028  
		0XA5, 0XC3, 0XA5, 0XB5, 0XA6, 0XA6, 0X87, 0X88, 0X88, 0X78, 0X87, 0X86,   //2029  
		0XA5, 0XB3, 0XA5, 0XA5, 0XA6, 0XA6, 0X88, 0X88, 0X88, 0X78, 0X87, 0X87,   //2030  
		0XA5, 0XB4, 0X96, 0XA5, 0X96, 0X96, 0X88, 0X78, 0X78, 0X78, 0X87, 0X87,   //2031  
		0X95, 0XB4, 0XA5, 0XB4, 0XA5, 0XA6, 0X97, 0X87, 0X87, 0X78, 0X87, 0X96,   //2032  
		0XA5, 0XC3, 0XA5, 0XB5, 0XA6, 0XA6, 0X88, 0X88, 0X88, 0X78, 0X87, 0X86,   //2033  
		0XA5, 0XB3, 0XA5, 0XA5, 0XA6, 0XA6, 0X88, 0X78, 0X88, 0X78, 0X87, 0X87,   //2034  
		0XA5, 0XB4, 0X96, 0XA5, 0XA6, 0X96, 0X88, 0X88, 0X78, 0X78, 0X87, 0X87,   //2035  
		0X95, 0XB4, 0XA5, 0XB4, 0XA5, 0XA6, 0X97, 0X87, 0X87, 0X78, 0X87, 0X96,   //2036  
		0XA5, 0XC3, 0XA5, 0XB5, 0XA6, 0XA6, 0X87, 0X88, 0X88, 0X78, 0X87, 0X86,   //2037  
		0XA5, 0XB3, 0XA5, 0XA5, 0XA6, 0XA6, 0X88, 0X88, 0X88, 0X78, 0X87, 0X87,   //2038  
		0XA5, 0XB4, 0X96, 0XA5, 0XA6, 0X96, 0X88, 0X88, 0X78, 0X78, 0X87, 0X87,   //2039  
		0X95, 0XB4, 0XA5, 0XB4, 0XA5, 0XA6, 0X97, 0X87, 0X87, 0X78, 0X87, 0X96,   //2040  
		0XA5, 0XC3, 0XA5, 0XB5, 0XA5, 0XA6, 0X87, 0X88, 0X87, 0X78, 0X87, 0X86,   //2041  
		0XA5, 0XB3, 0XA5, 0XB5, 0XA6, 0XA6, 0X88, 0X88, 0X88, 0X78, 0X87, 0X87,   //2042  
		0XA5, 0XB4, 0X96, 0XA5, 0XA6, 0X96, 0X88, 0X88, 0X78, 0X78, 0X87, 0X87,   //2043  
		0X95, 0XB4, 0XA5, 0XB4, 0XA5, 0XA6, 0X97, 0X87, 0X87, 0X88, 0X87, 0X96,   //2044  
		0XA5, 0XC3, 0XA5, 0XB4, 0XA5, 0XA6, 0X87, 0X88, 0X87, 0X78, 0X87, 0X86,   //2045  
		0XA5, 0XB3, 0XA5, 0XB5, 0XA6, 0XA6, 0X87, 0X88, 0X88, 0X78, 0X87, 0X87,   //2046  
		0XA5, 0XB4, 0X96, 0XA5, 0XA6, 0X96, 0X88, 0X88, 0X78, 0X78, 0X87, 0X87,   //2047  
		0X95, 0XB4, 0XA5, 0XB4, 0XA5, 0XA5, 0X97, 0X87, 0X87, 0X88, 0X86, 0X96,   //2048  
		0XA4, 0XC3, 0XA5, 0XA5, 0XA5, 0XA6, 0X97, 0X87, 0X87, 0X78, 0X87, 0X86,   //2049  
		0XA5, 0XC3, 0XA5, 0XB5, 0XA6, 0XA6, 0X87, 0X88, 0X78, 0X78, 0X87, 0X87    //2050   
	};

	
//	private static final String mJieqiArray[] = {"小寒","大寒","立春","雨水","惊蛰","春分",
//										 "清明","谷雨","立夏","小满","芒种","夏至",
//										 "小暑","大暑","立秋","处暑","白露","秋分",
//										 "寒露","霜降","立冬","小雪","大雪","冬至"};
//	
//	private static final String chineseNumberDay[] = {"初一", "初二", "初三", "初四", "初五", "初六", "初七", 
//														"初八", "初九", "初十", "十一", "十二", "十三","十四",
//														"十五", "十六", "十七", "十八", "十九", "二十", "廿一",
//														"廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八",
//														"廿九", "三十"};
//	
//	private static final String chineseNumberMonth[] = {"正月", "二月", "三月", "四月", "五月", "六月", "七月", 
//														"八月", "九月", "十月", "十一月", "腊月"};
	private String[] mJieqiArray;
	private String[] chineseNumberDay;
	private String[] chineseNumberMonth;
	private Context mContext;
	
	public ToLunarCalendar(Context context)
	{
		mContext = context;
		
		mJieqiArray = context.getResources().getStringArray(R.array.jieqi_array);
		chineseNumberDay = context.getResources().getStringArray(R.array.chinese_number_day);
		chineseNumberMonth = context.getResources().getStringArray(R.array.chinese_number_month);
	}
	
	private boolean isLeapYear(long year)
    {
		// 阳历闰年规定：公元年数可用4整除的，就算闰年；为了要在400年减去多算的3天，并规定公元世纪的整数，
		// 即公元年数是100的整数时，须用400来整除的才算闰年，如1600年、2000年、2200年、2400年就是闰年。
		// 这样就巧妙地在400年中减去了3天，阳历规定每年都是12个月，月份的大小完全是人为的规定，现在规定
		// 每年的1、3、5、7、8、10、12月为大月，每月31天；4、6、9、11月为小月，每月30天；2月平年是28天，闰年是29天。 

		return (year%4 == 0) && (year%100 != 0) || (year%400 == 0);
	}
	
	//                                             ----------------------------- 4
	public void transform(int iYear, int iMonth, int iDay) 
	{
		mYear = iYear;
		mMonth = iMonth;
		mDay = iDay;
		if(iYear > END_YEAR) 
			return;
		
		
		l_CalcLunarDate(CalcDateDiff(iYear, iMonth, iDay, START_YEAR, 1, 1)); 

		//return getLunarHoliDay(iYear, iMonth, iDay);
	} 
	
	//将阳历的日期转换成农历日期
	//iSpanDays为当前日期距离1901年1月1日的天数
	private void  l_CalcLunarDate(long iSpanDays)  
	{  
		//阳历1901年2月19日为阴历1901年正月初一  
		//阳历1901年1月1日到2月19日共有49天  
		int iYear, iMonth, iDay;
		
	    if(iSpanDays <49) 
	    { 
	        iYear = START_YEAR - 1; 
	        if(iSpanDays <19) 
	        { 
	            iMonth = 11; 
	            iDay = 11+(int)(iSpanDays); 
	        } 
	        else 
	        { 
	            iMonth = 12; 
	            iDay = (int)(iSpanDays) -18; 
	        } 
	        return; 
	    } 
	    //下面从阴历1901年正月初一算起 
	    iSpanDays -= 49; 
	    iYear = START_YEAR; 
	    iMonth = 1; 
	    iDay = 1; 
	    //计算年 
	    int tmp = LunarYearDays(iYear); 
	    while(iSpanDays >= tmp)  
	    {  
	        iSpanDays -= tmp;  
	        tmp = LunarYearDays(++iYear);  
	    } 
	    mLunarYear = iYear;
	    
	    //计算月  
	    tmp = 0x0000FFFF & (LunarMonthDays(iYear, iMonth));
	    while(iSpanDays >= tmp)  
	    {  
	    	iSpanDays -= tmp;  
	        if(iMonth == GetLeapMonth(iYear))  
	        {          
	            tmp  = (LunarMonthDays(iYear, iMonth)) >> 16;  
	            if(iSpanDays < tmp) 
	            {
	            	iMonth = iMonth*(-1);
	                break; 
	            }
	            iSpanDays -= tmp; 
	        } 
	        tmp = 0x0000FFFF & (LunarMonthDays(iYear, ++iMonth)); 
	    } 
	    mLunarMonth = iMonth;
	    //计算日 
	    iDay += (int)(iSpanDays); 
	    mLunarDay = iDay;
	}
	//-----end-------------------------
	
	
	//计算阳历的日期距离1901年1月1日的天数 - good
	private int CalcDateDiff(int iEndYear, int iEndMonth, int iEndDay,  
	                    	int iStartYear, int iStartMonth, int iStartDay)  
	{  
	    int monthday[]={0, 31, 59 ,90, 120, 151, 181, 212, 243, 273, 304, 334};  
	   
	//计算两个年份1月1日之间相差的天数  
	    int iDiffDays =(iEndYear - iStartYear)*365;  
	    iDiffDays += (iEndYear-1)/4 - (iStartYear-1)/4;  
	    iDiffDays -= ((iEndYear-1)/100 - (iStartYear-1)/100);  
	    iDiffDays += (iEndYear-1)/400 - (iStartYear-1)/400;  
	   
	    //加上iEndYear年1月1日到iEndMonth月iEndDay日之间的天数  
	    iDiffDays += monthday[iEndMonth-1] +  
	                           (isLeapYear(iEndYear)&&iEndMonth>2? 1: 0);  
	    iDiffDays += iEndDay;  
	   
	    //减去iStartYear年1月1日到iStartMonth月iStartDay日之间的天数  
	    iDiffDays -= (monthday[iStartMonth-1] +  
	                  (isLeapYear(iStartYear)&&iStartMonth>2 ? 1: 0));  
	    iDiffDays -= iStartDay;  
	    return iDiffDays;  
	}  
	
 
	
	private int LunarYearDays(int iLunarYear) 
	{ 
	    int days =0; 
	    for(int i=1; i<=12; i++) 
	    { 
	        long tmp = LunarMonthDays(iLunarYear ,i); 
	        days += (tmp) >> 16; 
	        days += 0x0000ffff & (tmp); 
	    } 
	    return days; 
	} 
	
	private int LunarMonthDays(int iLunarYear, int iLunarMonth)  
	{  
	    if(iLunarYear < START_YEAR) 
	        return 30; 

	    int height = 0;
	    int low = 29; 
	    int iBit = 16 - iLunarMonth; 

	    if(iLunarMonth > GetLeapMonth(iLunarYear) && GetLeapMonth(iLunarYear)!= 0)  
	        iBit --;  
	   
	    if((gLunarMonthDay[iLunarYear - START_YEAR]&(1<<iBit)) != 0) 
	        low ++; 

	    if(iLunarMonth == GetLeapMonth(iLunarYear)) 
	    {
	        if((gLunarMonthDay[iLunarYear - START_YEAR]&(1<<(iBit -1))) != 0 ) 
	            height =30; 
	        else 
	            height =29; 
	    }

	    return (height << 16) | low; 
	} 
	
	//返回当年的闰月的月份;0表示没有闰月
	private int GetLeapMonth(int iLunarYear)  
	{ 
	    char flag = gLunarMonth[(iLunarYear - START_YEAR)/2];
	    return  ((iLunarYear - START_YEAR)%2 != 0) ? (flag&0x000f) : ((flag>>4) & 0x000f);  
	}  
	
	//根据节气数据存储格式,计算阳历iYear年iMonth月iDay日对应的节气 ------------------------3
	public String getLunarHoliday(int iYear, int  iMonth, int  iDay) 
	{ 
		if(iYear > END_YEAR) 
			return null;
	    char flag = gLunarHolDay[(iYear - START_YEAR)*12 + iMonth -1]; 
	    int  day; 
	    if(iDay <15) 
	        day= 15 - ((flag>>4)&0x0f);  
	    else  
	        day = ((flag)&0x0f)+15;  
	    if(iDay == day)  
	       return mJieqiArray[(iMonth-1) *2 + (iDay>15? 1: 0)];  
	    else  
	       return null;  
	}
	
	public String getLunarHoliday()     //         -------------------3
	{
		if(mYear > END_YEAR) 
			return null;
		return getLunarHoliday(mYear, mMonth, mDay);
	}
	
	// 返回农历日期， 如 初二、闰三月、腊月、正月   ---------------------2
	public String getLunarDateString()
	{
		if(mYear > END_YEAR) 
			return null;
		
		if(mLunarDay == 1)
		{
			if(mLunarMonth < 0)
			{
				return mContext.getResources().getString(R.string.leap) + chineseNumberMonth[-1-mLunarMonth];
			}
			else
			{
				return chineseNumberMonth[mLunarMonth-1];
			}
		}
		else
		{
			return chineseNumberDay[mLunarDay-1];
		}
	}
	
    //====返回农历正月初一
    public String getLunarMonthDay() 
    {  
    	    if(mYear > END_YEAR) 
			return null;
    	
		if(mLunarMonth < 0)
		{
			return mContext.getResources().getString(R.string.leap) + 
				chineseNumberMonth[-1-mLunarMonth] + chineseNumberDay[mLunarDay-1];
		}
		else
		{
			return chineseNumberMonth[mLunarMonth-1]  + chineseNumberDay[mLunarDay-1];
		}
    }

    //====== 传回干支, 0=甲子   
    private String cyclicalm() 
    {   
    	int num = mLunarYear - 1900 + 36;
        //final String[] Gan = new String[]{"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};   
        //final String[] Zhi = new String[]{"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    	final String[] Gan = mContext.getResources().getStringArray(R.array.gan);
    	final String[] Zhi = mContext.getResources().getStringArray(R.array.zhi);
        return (Gan[num % 10] + Zhi[num % 12]);   
    }
    
    //====== 传回农历y年的生肖   
    private String animalsYear() 
    {   
        //final String[] Animals = new String[]{"鼠年", "牛年", "虎年", "兔年", "龙年", "蛇年", "马年", "羊年", "猴年", "鸡年", "狗年", "猪年"};
    	final String[] Animals = mContext.getResources().getStringArray(R.array.animals);
        return Animals[(mLunarYear - 4) % 12];   
    }
	
    // override                  ------------------------1
    public String toString() 
    {  
    	if(mYear > END_YEAR) 
			return null;
    	
		if(mLunarMonth < 0)
		{
			//return cyclicalm() + "." + animalsYear() + ".闰" + chineseNumberMonth[-1-mLunarMonth] + "." + chineseNumberDay[mLunarDay-1];
			//return cyclicalm() + "." + 
            //                animalsYear()  +mContext.getResources().getString(R.string.leap) + 
			//	    chineseNumberMonth[-1-mLunarMonth]  + chineseNumberDay[mLunarDay-1];
			return cyclicalm() + animalsYear() + mContext.getResources().getString(R.string.leap) + 
				    chineseNumberMonth[-1-mLunarMonth]+ chineseNumberDay[mLunarDay-1];
		}
		else
		{
			//return cyclicalm() + "." + animalsYear() + "." + chineseNumberMonth[mLunarMonth-1] + "月." + chineseNumberDay[mLunarDay-1];
			//return cyclicalm() + "." + 
            //                    animalsYear() +chineseNumberMonth[mLunarMonth-1] +  chineseNumberDay[mLunarDay-1];
			return cyclicalm() + animalsYear() +chineseNumberMonth[mLunarMonth-1]+ chineseNumberDay[mLunarDay-1];
		}
    }

    public String getCyclicalmAndaAnimalsYearAndMonthStr() 
    {  
    	if(mYear > END_YEAR) 
			return null;
    	
		if(mLunarMonth < 0){
			return cyclicalm() + animalsYear() + mContext.getResources().getString(R.string.leap) + 
				    chineseNumberMonth[-1-mLunarMonth];
		} else {
			return cyclicalm() + animalsYear() +chineseNumberMonth[mLunarMonth-1];
		}
    }

	public String getCyclicalmAndaAnimalsYear() 
    {  
    	if(mYear > END_YEAR) 
			return null;
    	
		if(mLunarMonth < 0){
			return cyclicalm() + animalsYear() + mContext.getResources().getString(R.string.leap);
		} else {
			return cyclicalm() + animalsYear();
		}
    }
    
    public int getLunarDay()
    {
    	if(mYear > END_YEAR) 
			return 0;
    	
    	return mLunarDay;
    }

/*Start of  pengshijie 2010.5.5 for  */
    public int getLunarMonth()
    {
    	if(mYear > END_YEAR) 
			return 0;

		
    	return Math.abs(mLunarMonth);
    }	
/*End   of  pengshijie 2010.5.5 for  */

}

