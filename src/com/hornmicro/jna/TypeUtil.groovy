package com.hornmicro.jna

import static com.hornmicro.jna.ObjectiveC.Utils.*
import com.sun.jna.Pointer

class TypeUtil {
	
	interface TypeMapper {
		public Object cToJ(Object cVar, String signature)
		public Object jToC(Object jVar, String signature)
	}
	
	class ScalarMapper implements TypeMapper {
			public Object cToJ(Object cVar, String signature) {
				char firstChar = signature.charAt(0)
				if ( Long.class.isInstance(cVar) || long.class.isInstance(cVar)){
					long cObj = (Long)cVar
					switch (firstChar){
						case 'i':
						case 'I':
						case 's':
						case 'S':
							return new Long(cObj).intValue()
						case 'c':
							return new Long(cObj).byteValue()
						case 'B':
							return cObj > 0L ? true:false
					}
				}
				return cVar
			}
		
			public Object jToC(Object jVar, String signature) {
				return jVar
			}
	}
	
	class PointerMapper implements TypeMapper {
		public Object cToJ(Object cVar, String signature) {
			if ( Pointer.isInstance(cVar)) return cVar
			return new Pointer((Long)cVar)
		}
		public Object jToC(Object jVar, String signature) {
			return jVar
		}
	}
	
	class StringMapper implements TypeMapper {
		public Object cToJ(Object cVar, String signature) {
			return new Pointer((Long)cVar).getString(0)
		}
		public Object jToC(Object jVar, String signature) {
			return jVar
		}
	}
	
	class NSObjectMapper implements TypeMapper {
		public Object cToJ(Object cVar, String signature) {
			//println("Mapping NSObject to Java "+cVar+" sig: "+signature);
	        Pointer cObj = Pointer.NULL
	        if ( Pointer.class.isInstance(cVar) ){
	            cObj = (Pointer)cVar
	        } else if (long.class.isInstance(cVar) || Long.class.isInstance(cVar) ){
	            cObj = new Pointer((Long)cVar)
	        } else {
	            return cVar
	        }
	        if ( (Pointer.NULL == cObj) || (cVar == null) || (cObj == null) || (cObj.peer == 0L ) ){
	            return null
	        }
	        String className = clsNameObj(cObj)
//	        if ( "NSString".equals(className) || "__NSCFString".equals(className)){
//				return nsStringToString(cObj)
//	        }
			
			Class type = TypeUtil.typeRegistry[className]
			if(type) {
				ObjectiveCProxy item = type.newInstance()
				item.setPointer(cObj)
				return item
			}  else {
				return new ObjectiveCProxy(cObj) 
			}
		}
		public Object jToC(Object jVar, String signature) {
			if ( jVar instanceof ObjectiveCProxy ){
				return ((ObjectiveCProxy)jVar).getPtr()
			} else {
				return jVar
			}
		}
	}
	
	class StructureMapper implements TypeMapper {
		public Object cToJ(Object cVar, String signature) {
			return cVar
		}
		public Object jToC(Object jVar, String signature) {
			return jVar
		}
	}
	
	static Map<String, Class> typeRegistry = [:]
	static boolean isRegistered(String type) {
		return typeRegistry.containsKey(type)
	}
	static registerType(String type, Class proxyClass, Pointer classPtr) {
		Object instance = msgSend(msgSend(classPtr, "alloc"), "init")
		String instanceType = clsNameObj(instance)
		//msgSend(instance, "dealloc")
		println "Adding $type and $instanceType to typeRegistry"
		synchronized(typeRegistry) {
			typeRegistry[type] = proxyClass
			if(instanceType != type && instanceType != "nil") {
				typeRegistry[instanceType] = proxyClass
			}
		}
	}
	
	
	Map<String, TypeMapper> mappers = [:]
	
	public TypeUtil() {
		addMapping(new ScalarMapper(), 'cCiIsSfdlLqQB[:b?#v'.split(''))
		addMapping(new PointerMapper(), '^')
		addMapping(new StringMapper(), '*')
		addMapping(new NSObjectMapper(), '@')
		addMapping(new StructureMapper(), '{')
	}
	
	private addMapping(TypeMapper mapper, String... signature) {
		for(String c: signature) {
			mappers[c] = mapper
		}
	}
	
	private String removeSignaturePrefix(String signature) {
		if(!signature) return signature
		String prefixes = "rnNoORV"
		int offset = 0
		while ( prefixes.indexOf(signature[offset]) != -1 ){
			offset++
			if ( offset > signature.length()-1 ){
				break
			}
		}
		if ( offset > 0 ){
			signature = signature.substring(offset)
		}
		return signature
	}
	
	public Object cToJ(Object cVar, String signature) {
		signature = removeSignaturePrefix(signature)
		String firstChar = signature.substring(0,1)
		TypeMapper mapper = mappers.get(firstChar)
		//println "Converting return type ${firstChar} using ${mapper}"
		if ( mapper == null ){
			
			// We couldn't find a mapper for this type
			throw new RuntimeException("No mapper registered for type "+firstChar)
		} else {
			return mapper.cToJ(cVar, signature)
		}
	}
	
	public Object jToC(Object jVar, String signature) {
		signature = removeSignaturePrefix(signature)
		
		String firstChar = signature.substring(0,1)
		TypeMapper mapper = mappers.get(firstChar)
		//println "Converting arg type ${firstChar} using ${mapper}"
		if ( mapper == null ) {
			
			// We couldn't find a mapper for this type
			throw new RuntimeException("No mapper registered for type "+firstChar)
		} else {
			return mapper.jToC(jVar, signature)
		}
	}
}
