package com.knowology.DataLayer;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.knowology.dal.Database;

public class Qh {
//	public int update(String classify, String className, List<Object> param) throws SQLException{
//		
//		return 0;
//	}
	
	public static void main(String args[]){
		String str = "fdsfhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh" +
				"hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh" +
				"hjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjfweuiiiiiiiiiiiiiiiiiircvn,xfjdksalfsfjdafl" +
				"jfsadksdddddddddddddddddddddddddddddddddddddddddddaf" +
				"faaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaj" +
				"dshfdsfdaskkkkkkkkkkkkkkkkkkkfdshakkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk" +
				"kkkkkkkkkkkkkkkkkkkkklfhdaskjfhafdspaufioasufppppppppppppppppppppfiodsaufoiadsufsaoidfusio" +
				"fadsuyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyfp" +
				"fuiasydifdsiyof fdsapfudsifhisapydfshdpfiudsyaufhsapdfiudshfdpsopiufdhsfks" +
				"fhadsipfhhhhhhhhhhhhhhhhohfdasofuioashfiosadufshadofipsadhffffffffffffffffffffhfioadsfhsaodipfh" +
				"hfdsafiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiifiodashfopsadjifioshpafhidsh" +
				"fhasudddddddddddddddddddddddfffffffffffffffffffffffffffffffffffffffffffffffffff" +
				"fiodsapfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
				"fhdsappppppppppppppppppppppppppppppppfidsofiosfiosdfoisdufosdufiosdufiosdfiosdfdsifiodsfdsiofdsi" +
				"fjdsoipafjiasodjfidsoapofjiooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
				"sfhdsssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
				"fjdsiaaaaaaaaaaaaaapwfsodjfweyojrweoifdojdfasnvidsoifjsaiojfidsafhdsoipfejfwoiaufodsfiweofjkds" +
				"fieuwfjdsopfidsaofewifpewifn;awfldskfopsdjfndsjfcjdsfsomnfidcmfdoscmpficopwjmcfdspfjdkcnhdsopacmfxsa" +
				"sdfndshfckfoindfhhsdjfsdhcmflskifochsdjcosdiihvfsdkfdnshcnkdjilfcnsofcjfkldsuocndsjcnsdio" +
				"hfudwincfdnhfkdsjcfosdofidnsopcfjdsniojfcosdifvodsjfvdsionfocndisfucdsnkfcosadifjcodfviosdovn";
		String sql = "insert into aaa(id, content) values (2, ?)";
		Clob clob = stringToClob(str);
		System.out.println(clob);
		try {
			List<Object> param = new ArrayList<Object>();
			// param.add(1);
			param.add(clob);
			Database.executeNonQueryReport(sql, param.toArray());
			// Database.executeNonQueryReport(sql);
			System.out.println("插入成功");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static Clob stringToClob(String str) {  
	    if (null == str) { 
	        return null;  
	    } else {  
	    	try {  
	            java.sql.Clob c = new javax.sql.rowset.serial.SerialClob(str.toCharArray());  
	            return c;  
	        } catch (Exception e) {  
	            return null;  
	        }  
	    }  
	}
	
	public static void find(String classify, String className, List<Object> param) throws SQLException{
		if(null == classify){
			
		}
	}
}
