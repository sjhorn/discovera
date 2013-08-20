package com.hornmicro.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public interface MacTypes {

	public static class ProcessSerialNumber extends Structure {
		public int highLongOfPSN; 
		public int lowLongOfPSN;
		
		public ProcessSerialNumber() {
			super();
		}
		
		public ProcessSerialNumber(int highLongOfPSN, int lowLongOfPSN) {
			super();
			this.highLongOfPSN = highLongOfPSN;
			this.lowLongOfPSN = lowLongOfPSN;
		}

		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] { "highLongOfPSN", "lowLongOfPSN"});
		}		 
	}
	
}
