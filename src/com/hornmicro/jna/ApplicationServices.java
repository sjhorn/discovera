package com.hornmicro.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public interface ApplicationServices extends Library {
	ApplicationServices INSTANCE = (ApplicationServices) Native.loadLibrary("ApplicationServices", ApplicationServices.class);
	
	int AESendMessage(AppleEvent event, AppleEvent reply, int sendMode, NativeLong timeOutInTicks);
	int AEGetParamDesc(AppleEvent event, int theAEKeyword, int desiredType, AEDesc result);
	
	public class AppleEvent extends AEDesc {
		public AppleEvent() {
			super();
		}

		public AppleEvent(int descriptorType, Pointer dataHandle) {
			super(descriptorType, dataHandle);
		}

		public AppleEvent(Pointer p) {
			super(p);
		}
		
	}
	
	public class AEDesc extends Structure {
		public int descriptorType;
		public Pointer dataHandle;
		public AEDesc(Pointer p) {
			super(p);
			read();
		}
		public AEDesc() {
			super();
		}
		public AEDesc(int descriptorType, Pointer dataHandle) {
			super();
			this.descriptorType = descriptorType;
			this.dataHandle = dataHandle;
		}
		
		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] {"descriptorType", "dataHandle"});
		}
		
		public static class ByReference extends AEDesc implements Structure.ByReference { }
		public static class ByValue extends AEDesc implements Structure.ByValue { }
	}
}
