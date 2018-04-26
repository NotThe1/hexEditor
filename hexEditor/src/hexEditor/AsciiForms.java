package hexEditor;


public  class AsciiForms {
	private static  AsciiForms instance = new AsciiForms();
	
	private byte byteForm;
	private String stringForm;
	private String asciiForm;
	private boolean isPrintable;
	
	private AsciiForms() {
	// singleton	
	}// private constructor
	
	public static AsciiForms getInstance() {
		return instance;
	}//getInstance
	
	public byte getByteForm() {
		return byteForm;
	}//getByteForm
	
	public String getStringForm() {
		return stringForm;
	}//getStringForm
	
	public String getAsciiForm() {
		return asciiForm;
	}//getStringForm
	
	public boolean isPrintable() {
		return isPrintable;
	}//isPrintable
	
	public void setByte(int byteValue) {
		setByte((byte) byteValue);
	}//setByte
	
	public void setByte(byte byteValue) {
		asciiForm = new String(new byte[] {(byte) byteValue});
		asciiForm =  PRINTABLES.indexOf(asciiForm)==-1?UNPRINTABLE:asciiForm;			
		stringForm = String.format("%02X", byteValue);
	}//setByte
	
	public void setString(String stringValue) {
		Integer x = Integer.parseInt(stringValue, 16);
		byteForm = x.byteValue();
		
		asciiForm = new String(new byte[] {(byte) byteForm});
		asciiForm = PRINTABLES.indexOf(asciiForm)==-1?UNPRINTABLE:asciiForm;
	}//setString
	
	public boolean setAscii(String asciiValue) {
		if( PRINTABLES.indexOf(asciiValue)==-1) { // unprintable - ignore
			stringForm = EMPTY_STRING;
			isPrintable = false;
		}else { // valid ASCII character
			stringForm = String.format("%02X", asciiValue.getBytes()[0]);
			isPrintable = true;
		}//if printable	
		
//		Integer x = Integer.parseInt(stringForm, 16);
//		byteForm = x.byteValue();
		
		byteForm = Byte.parseByte(stringForm,16);
		return isPrintable;
	}//setAscii
	
	public static final String EMPTY_STRING = "";
	public static final String UNPRINTABLE = ".";

	/* @formatter:off */
	public static final String PRINTABLES = " !\"#$%&'()*+,-./" +
	                                        "0123456789:;<=>?" +
			                                "@ABCDEFGHIJKLMNO" +
	                                        "PQRSTUVWXYZ[\\]^_" +
			                                "`abcdefghijklmno" +
	                                        "pqrstuvwxyz{|}~" ;
/* @formatter:on  */

}//class AsciiForms
